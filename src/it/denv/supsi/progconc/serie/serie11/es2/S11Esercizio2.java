package it.denv.supsi.progconc.serie.serie11.es2;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
	My results:

	$ cat /proc/cpuinfo | grep 'model name' | head -n 1
		model name      : Intel(R) Core(TM) i7-6700HQ CPU @ 2.60GHz
	$ nproc
		8
	$ for i in {1..17}; do echo $i; java -Xms2048m -Xmx4G \
	  it.denv.supsi.progconc.serie.serie11.es2.S11Esercizio3 $i; done

		1
		Runtime: 16289ms
		2
		Runtime: 8232ms
		3
		Runtime: 6194ms
		4
		Runtime: 4558ms
		5
		Runtime: 4042ms
		6
		Runtime: 3275ms
		7
		Runtime: 3051ms
		8
		Runtime: 2894ms
		9
		Runtime: 2693ms
		10
		Runtime: 2577ms
		11
		Runtime: 2515ms
		12
		Runtime: 2647ms
		13
		Runtime: 2542ms
		14
		Runtime: 2495ms
		15
		Runtime: 2549ms
		16
		Runtime: 2453ms
		17
		Runtime: 2435ms

 */

/**
 * This JPanel holds the image
 */
class ImagePanel extends JPanel {
	private static final long serialVersionUID = -765326845521113343L;

	// contains the image that is computed by this program
	final BufferedImage image;
	private final JPanel imagePanel;

	public ImagePanel(final int w, final int h) {
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		paintGray();

		// imagePanel is a JPanel that draws the image data
		imagePanel = new JPanel() {
			private static final long serialVersionUID = 4002004872041961024L;

			@Override
			protected void paintComponent(final Graphics g) {
				if (image == null)
					// fill with background color, gray
					super.paintComponent(g);
				else {
					// Multiple access to update image!
					synchronized (image) {
						g.drawImage(image, 0, 0, null);
					}
				}
			}
		};
		imagePanel.setPreferredSize(new Dimension(w, h));
		setLayout(new BorderLayout());
		add(imagePanel, BorderLayout.CENTER);
	}

	/**
	 * Adds the give rowData to the image and updates the image
	 *
	 * @param rowData
	 * @param row
	 */
	public void setRowAndUpdate(final int[] rowData, final int row) {
		final int width = getWidth();

		// Image is a shared resource!
		synchronized (image) {
			image.setRGB(0, row, width, 1, rowData, 0, width);
		}
		// Repaint just the newly computed row.
		imagePanel.repaint(0, row, width, 1);
	}

	private void paintGray() {
		final Graphics g = image.getGraphics();
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.dispose();
	}

	public void resetImage() {
		paintGray();
		imagePanel.repaint();
	}

	@Override
	public int getWidth() {
		return image.getWidth();
	}

	@Override
	public int getHeight() {
		return image.getHeight();
	}
}

/**
 * Mandelbrot generator class
 */
class Mandelbrot {
	private final int[] palette = new int[256];
	private final double xmin = -1.6744096740931858;
	private final double xmax = -1.6744096740934730;
	private final double ymin = 4.716540768697223E-5;
	private final double ymax = 4.716540790246652E-5;
	private final int maxIterations = 10000;

	private final int width;
	private final int height;
	private final double dx;
	private final double dy;

	public Mandelbrot(final int width, final int heigth) {
		this.width = width;
		this.height = heigth;
		dx = (xmax - xmin) / (width - 1);
		dy = (ymax - ymin) / (height - 1);

		for (int i = 0; i < 256; i++)
			palette[i] = Color.getHSBColor(i / 255F, 1, 1).getRGB();
	}

	/**
	 * Returns the imageData for given row
	 *
	 * @return
	 */
	public int[] computeRow(final int row) {
		final int[] rgbRow = new int[width];
		final double y = ymax - dy * row;
		for (int col = 0; col < width; col++) {
			final double x = xmin + dx * col;
			final int count = computePoint(x, y);
			if (count == maxIterations)
				rgbRow[col] = 0;
			else
				rgbRow[col] = palette[count % palette.length];
		}
		return rgbRow;
	}

	private int computePoint(final double x, final double y) {
		int count = 0;
		double xx = x;
		double yy = y;
		while (count < maxIterations && (xx * xx + yy * yy) < 4) {
			count++;
			final double newxx = xx * xx - yy * yy + x;
			yy = 2 * xx * yy + y;
			xx = newxx;
		}
		return count;
	}
}

public class S11Esercizio2 extends JPanel {
	private static final long serialVersionUID = -765326845524613343L;

	// the threads that compute the image
	private int workers;

	// used to signal the thread to abort
	public volatile boolean running;
	// how many threads have finished running?
	private volatile int threadsCompleted;
	// button the user can click to start or abort the thread
	private final JButton startButton;
	public Object notifier = new Object();

	// for specifying the number of threads to be used
	public final JComboBox<Integer> threadCountSelect;
	private final ImagePanel imagePanel;
	private final Mandelbrot fractal;

	/**
	 */
	public S11Esercizio2() {

		setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		setLayout(new BorderLayout());

		// Top - Label, ComboBox and Button
		final JPanel topPanel = new JPanel();
		startButton = new JButton("Start");
		topPanel.add(startButton);
		threadCountSelect = new JComboBox<Integer>();
		for (int i = 1; i <= 32; i++) {
			threadCountSelect.addItem(i);
		}

		threadCountSelect.setSelectedIndex(Runtime.getRuntime().availableProcessors() - 1);

		topPanel.add(new JLabel("Number of threads to use: "));
		topPanel.add(threadCountSelect);

		topPanel.setBackground(Color.LIGHT_GRAY);
		add(topPanel, BorderLayout.NORTH);
		startButton.addActionListener(e -> {
			if (running) {
				stop();
			}
			else {
				start();
			}
		});

		// Main - Image Panel

		DisplayMode dm = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice()
				.getDisplayMode();

		final int width = dm.getWidth() - 200;
		final int height = dm.getHeight() - 100;

		imagePanel = new ImagePanel(width, height);
		add(imagePanel, BorderLayout.CENTER);
		fractal = new Mandelbrot(width, height);
	}

	/**
	 * This method is called when the user clicks the Start button, while no
	 * computation is in progress. It starts as many new threads as the user has
	 * specified, and assigns a different part of the image to each thread. The
	 * threads are run at lower priority than the event-handling thread, in
	 * order to keep the GUI responsive.
	 */
	void start() {
		// change name while computation is in progress
		startButton.setText("Abort");
		imagePanel.resetImage();

		// will be re-enabled when all threads finish
		threadCountSelect.setEnabled(false);

		final int threadCount = (int) threadCountSelect.getSelectedItem();
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

		// How many rows of pixels should each thread compute?
		int rowsPerThread;

		final int height = imagePanel.getHeight();

		rowsPerThread = height / threadCount;

		running = true;
		threadsCompleted = 0;

		for (int i = 0; i < threadCount; i++) {
			final int startRow; // first row computed by thread number i
			final int endRow; // last row computed by thread number i
			// Create and start a thread to compute the rows of the image from
			// startRow to endRow. Note that we have to make sure that
			// the endRow for the last thread is the bottom row of the image.
			startRow = rowsPerThread * i;
			if (i == threadCount - 1)
				endRow = height - 1;
			else
				endRow = rowsPerThread * (i + 1) - 1;
			final String threadName = "WorkerThread " + (i + 1) + "/"
					+ threadCount;

			executorService.submit(new DrawingTask(startRow, endRow));
			workers++;
		}

		executorService.shutdown();
	}

	/**
	 * Called when the user clicks the button while a thread is running. A
	 * signal is sent to the thread to terminate, by setting the value of the
	 * signaling variable, running, to false.
	 */
	void stop() {
		// will be re-enabled when all threads finish
		startButton.setEnabled(false);
		running = false;
	}

	/**
	 * Called by each thread upon completing it's work
	 */
	synchronized void threadFinished() {
		threadsCompleted++;
		if (threadsCompleted == workers) {
			// all threads have finished
			startButton.setText("Start");
			startButton.setEnabled(true);
			// Make sure running is false after the thread ends.
			running = false;
			synchronized (notifier) {
				notifier.notify();
			}

			workers = 0;
			threadCountSelect.setEnabled(true); // re-enable pop-up menu
			imagePanel.repaint();
		}
	}

	/**
	 * Program starting point
	 */
	public static void main(final String[] args) {
		final JFrame window = new JFrame("Multiprocessing Demo 1");
		final S11Esercizio2 content = new S11Esercizio2();

		window.setContentPane(content);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setResizable(false);
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation((screenSize.width - window.getWidth()) / 2, (screenSize.height - window.getHeight()) / 2);
		window.setVisible(true);

		content.threadCountSelect.setSelectedIndex(Integer.valueOf(args[0]) - 1);

		long now = System.currentTimeMillis();
		content.start();
		synchronized(content.notifier) {
			while (content.running) {
				try {
					content.notifier.wait();
				} catch (InterruptedException e) {
					break;
				}
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Runtime: " + (end-now) + "ms");
		System.exit(0);
	}

	class DrawingTask implements Runnable {

		private int startRow;
		private int endRow;

		DrawingTask(int startRow, int endRow){
			this.startRow = startRow;
			this.endRow = endRow;
		}

		@Override
		public void run() {
			try {
				for (int row = startRow; row <= endRow; row++) {
					final int[] rgbRow = fractal.computeRow(row);
					if (!running)
						return;
					imagePanel.setRowAndUpdate(rgbRow, row);
				}
			} finally {
				threadFinished();
			}
		}
	}
}
