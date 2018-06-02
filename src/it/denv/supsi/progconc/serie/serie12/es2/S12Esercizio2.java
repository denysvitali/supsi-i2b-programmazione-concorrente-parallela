package it.denv.supsi.progconc.serie.serie12.es2;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

final class Coordinate {
	private final double lat;
	private final double lon;

	public Coordinate(final double lat, final double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	/**
	 * Returns the distance (expressed in km) between two coordinates
	 *
	 * @param from
	 * @return Returns the distance expressed in km
	 */
	public double distance(final Coordinate from) {
		final double earthRadius = 6371.000; // km
		final double dLat = Math.toRadians(from.lat - this.lat);
		final double dLng = Math.toRadians(from.lon - this.lon);
		final double a = Math.sin(dLat / 2.0) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(from.lat))
				* Math.cos(Math.toRadians(this.lat)) * Math.sin(dLng / 2.0) * Math.sin(dLng / 2.0);
		final double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return (earthRadius * c);
	}

	@Override
	public String toString() {
		return String.format("[%.5f, %.5f]", lat, lon);
	}
}

class Earthquake implements Comparable<Earthquake> {
	private final static String CSV_REGEX = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

	private final Date time;
	private final Coordinate position;
	private final double depth;
	private final double magnitude;
	private final String place;

	public Earthquake(final Date time, final Coordinate pos, final double depth, final double mag, final String place) {
		this.time = time;
		this.position = pos;
		this.depth = depth;
		this.magnitude = mag;
		this.place = place;
	}

	public Coordinate getPosition() {
		return position;
	}

	public double getMagnitude() {
		return magnitude;
	}

	public double getDepth() {
		return depth;
	}

	public int getDepthCategory() {
		return (int) (Math.ceil(getDepth() / 100) * 100);
	}

	public static Earthquake parse(final String csvLine) {
		final String[] splits = csvLine.split(CSV_REGEX);
		if (splits.length != 15) {
			System.out.println("Failed to parse: " + csvLine);
			return null;
		}

		final Date time;

		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			time = sdf.parse(splits[0]);
		} catch (final ParseException e) {
			return null;
		}

		final double lat = tryParseDouble(splits[1]);
		final double lon = tryParseDouble(splits[2]);
		final double depth = tryParseDouble(splits[3]);
		final double mag = tryParseDouble(splits[4]);
		final String place = splits[13];

		return new Earthquake(time, new Coordinate(lat, lon), depth, mag, place);
	}

	private static Double tryParseDouble(final String str) {
		try {
			return Double.parseDouble(str);
		} catch (final NumberFormatException e) {
			return new Double(0);
		}
	}

	@Override
	public String toString() {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(time) + " mag: " + magnitude
				+ " depth: " + depth + "km @ " + position + " " + place;
	}

	@Override
	public int compareTo(Earthquake earthquake) {
		return Double.compare(this.getDepthCategory(), earthquake.getDepthCategory());
	}

	public int getMagnitudeCategory() {
		return (int) Math.ceil(getMagnitude());
	}
}

public class S12Esercizio2 {

	private static boolean parallel = true;
	// Parallel = false : 3851 ms
	// Parallel = true  : 3542 ms

	private static CompletableFuture<List<Earthquake>> loadEarthquakeDB(final String address, final boolean isLocalFile) {
		final List<Earthquake> quakes = new ArrayList<>();
		return CompletableFuture.supplyAsync(() ->
				{
					final Reader reader;
					if (isLocalFile) {
						try {
							final File file = new File(address);
							reader = new FileReader(file);
						} catch (final FileNotFoundException e2) {
							System.out.println("Failed to open file: " + address);
							return Collections.emptyList();
						}
					} else {
						final URL url;
						try {
							url = new URL(address);
						} catch (final MalformedURLException e) {
							System.out.println("Failed to create URL for address: " + address);
							return Collections.emptyList();
						}
						final InputStream is;
						try {
							is = url.openStream();
						} catch (final IOException e) {
							System.out.println("Failed to open stream for: " + address);
							return Collections.emptyList();
						}
						reader = new InputStreamReader(is);
					}

					System.out.println("Requesting earthquake data from: " + address + " ...");

					String line;
					try {
						final BufferedReader br = new BufferedReader(reader);
						line = br.readLine();
						while ((line = br.readLine()) != null) {
							final Earthquake quake = Earthquake.parse(line);
							if (quake != null)
								quakes.add(quake);
							else
								System.out.println("Failed to parse: " + line);

						}

						br.close();
					} catch (final IOException e) {
						e.printStackTrace();
					}
					return quakes;
				}
		);
	}

	public static void main(final String[] args) {
		final String URI = Paths.get("src/it/denv/supsi/progconc/serie/serie12/es1/2014-2015.csv").toAbsolutePath().toString();
		System.out.println(URI);
		final long startTime = System.currentTimeMillis();

		// Completed in 3564 ms (computation time=280 ms)

		CompletableFuture<List<Earthquake>> quakes = loadEarthquakeDB(URI, true);
		AtomicLong computeTime = new AtomicLong();

		CompletableFuture<List<Earthquake>> ql = quakes.thenCompose(
				q -> CompletableFuture.supplyAsync(() -> {
					if (q == null || q.isEmpty()) {
						throw new RuntimeException("Earthquake list empty.");
					}
					return q;
				}))
				.handle((v, e)->{
					if(e != null){
						System.out.println(e.getMessage());
						System.exit(0);
					}
					System.out.println("Loaded " + v.size() + " earthquakes");
					computeTime.set(System.currentTimeMillis());
					return v;
				});

		final Coordinate supsi = new Coordinate(46.0234, 8.9172);

		List<CompletableFuture<Boolean>> futures = new ArrayList<>();
		ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		futures.add(ql.thenCompose(q-> CompletableFuture.supplyAsync(()->{
			System.out.println("Searching for nearest earthquake ...");

			AbstractMap.SimpleEntry<Earthquake, Double> nearest =
					q.stream()
							.map(e -> new AbstractMap.SimpleEntry<>(e, e.getPosition().distance(supsi)))
							.min(Comparator.comparingDouble(AbstractMap.SimpleEntry::getValue))
							.get();
			System.out.println("Nearest Earthquake: " + nearest);
			return true;
		}, es)));

		futures.add(ql.thenCompose(q->CompletableFuture.supplyAsync(()->{
			AbstractMap.SimpleEntry<Earthquake, Double> strongest =
					q.stream()
							.map(e -> new AbstractMap.SimpleEntry<>(e, e.getMagnitude()))
							.max(Comparator.comparingDouble(AbstractMap.SimpleEntry::getValue))
							.get();
			System.out.println("Strongest Earthquake: " + strongest);
			return true;
		}, es)));

		futures.add(ql.thenCompose(q->CompletableFuture.supplyAsync(()->{
			AbstractMap.SimpleEntry<Earthquake, Double> strongest =
					q.stream()
							.map(e -> new AbstractMap.SimpleEntry<>(e, e.getMagnitude()))
							.max(Comparator.comparingDouble(AbstractMap.SimpleEntry::getValue))
							.get();

			System.out.println("Nearest 10 Earthquakes w/ magnitude between 4 and 6, which are at least 2000km away:");
			q.stream()
					.map(e -> new AbstractMap.SimpleEntry<>(e, e.getPosition().distance(supsi)))
					.filter(e-> e.getValue() > 2000)
					.filter(e-> e.getKey().getMagnitude() > 4 && e.getKey().getMagnitude() < 6)
					.sorted(Comparator.comparingDouble(AbstractMap.SimpleEntry::getValue))
					.limit(10)
					.map(e-> String.format("%s (%f km away)", e.getKey(), e.getValue()))
					.forEach(System.out::println);
			return true;
		}, es)));

		futures.add(ql.thenCompose(q->CompletableFuture.supplyAsync(()->{
			long lat46 = q.stream()
					.filter(e->{double lat = e.getPosition().getLat(); return lat >= 46 && lat < 47; })
					.count();
			System.out.println(String.format("%d terremoti con latitudine 46", lat46));
			return true;
		}, es)));

		futures.add(ql.thenCompose(q->CompletableFuture.supplyAsync(()->{
			long lng8 = q.stream()
					.filter(e->{double lng = e.getPosition().getLon(); return lng >= 8 && lng < 9; })
					.count();
			System.out.println(String.format("%d terremoti con longitudine 8", lng8));
			return true;
		}, es)));

		futures.add(ql.thenCompose(q->CompletableFuture.supplyAsync(()->{
			System.out.println("Quakes by depth ranges:");
			// Terremoti per fasce di profondità
			q.stream()
					.collect(
							Collectors.groupingByConcurrent(
									Earthquake::getDepthCategory,
									Collectors.counting()
							)
					)
					.entrySet()
					.parallelStream()
					.sorted(Comparator.comparingInt(Map.Entry::getKey))
					.forEach(e->
							System.out.println(e.getKey() + " : " + e.getValue()));
			return true;
		}, es)));

		futures.add(ql.thenCompose(q->CompletableFuture.supplyAsync(()->{
			System.out.println("Quakes by magnitude ranges:");
			// Terremoti per fasce d'intensità
			q.stream()
					.collect(
							Collectors.groupingByConcurrent(
									Earthquake::getMagnitudeCategory,
									Collectors.counting()
							)
					)
					.entrySet()
					.parallelStream()
					.sorted(Comparator.comparingInt(Map.Entry::getKey))
					.forEach(e->
							System.out.println(e.getKey() + " : " + e.getValue()));
			return true;
		}, es)));


		CompletableFuture<Void> combined = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		try {
			combined.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("Completed in " + ((endTime - startTime)) + " ms" + " (computation time=" + (endTime - computeTime.get()) + " ms)");
	}
}
