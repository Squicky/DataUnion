import java.util.Vector;


public class CellIdentification {

	public int CellID = -1;
	
	public String WCDMA_Ch;
	public String WCDMA_SC;
	public String GSM_CellId;
	public String GSM_LAC;

	public double min_latitude = Double.MAX_VALUE;
	public double min_longitude = Double.MAX_VALUE;
	public double max_latitude = Double.MIN_VALUE;
	public double max_longitude = Double.MIN_VALUE;

	public static double DistanceToNextCell = 5000;
	
	public Vector<Dataset> Datasets = new Vector<Dataset>();
	
	public CellIdentification(int id, Dataset od) {
		this.CellID = id;
		this.WCDMA_Ch = od.WCDMA_Ch;
		this.WCDMA_SC = od.WCDMA_SC;
		this.GSM_CellId = od.GSM_CellId;
		this.GSM_LAC = od.GSM_LAC;
		this.addToGroup(od);
	}
	
	public boolean isInGroup(Dataset od) {

		if (this.WCDMA_Ch.equals(od.WCDMA_Ch) && this.WCDMA_SC.equals(od.WCDMA_SC) && 
				this.GSM_CellId.equals(od.GSM_CellId) && this.GSM_LAC.equals(od.GSM_LAC)) {

			double lat = max_latitude - min_latitude;
			lat = lat / 2;
			lat += min_latitude;
			
			double lon = max_longitude - min_longitude;
			lon /= 2;
			lon += min_longitude;
			
			double d = CellIdentification.getDistance(lat, lon, od.matched_latitude, od.matched_longitude);
			
			if ( d <= DistanceToNextCell ) {
				return true;
			} else {
				d++;
			}
		}
		
		return false;
	}
	
	public void addToGroup(Dataset od) {
		if (od.matched_latitude < min_latitude) {
			min_latitude = od.matched_latitude;
		} 
		if (max_latitude < od.matched_latitude) {
			max_latitude = od.matched_latitude;
		}
		
		if (od.matched_longitude < min_longitude) {
			min_longitude = od.matched_longitude;
		} 
		if (max_longitude < od.matched_longitude) {
			max_longitude = od.matched_longitude;
		}
		
		od.CellId = CellID;
		Datasets.add(od);
	}
	
	public void copyDatasetsFrom(CellIdentification cid2) {
		for (int i=0; i<cid2.Datasets.size(); i++) {
			Dataset ds = cid2.Datasets.elementAt(i);
			
			this.addToGroup(ds);
		}
	}
	
	public static double getDistance(CellIdentification cid1, CellIdentification cid2) {

		double lat1 = cid1.max_latitude - cid1.min_latitude;
		lat1 = lat1 / 2;
		lat1 += cid1.min_latitude;
		
		double lon1 = cid1.max_longitude - cid1.min_longitude;
		lon1 /= 2;
		lon1 += cid1.min_longitude;

		double lat2 = cid2.max_latitude - cid2.min_latitude;
		lat2 = lat2 / 2;
		lat2 += cid2.min_latitude;
		
		double lon2 = cid2.max_longitude - cid2.min_longitude;
		lon2 /= 2;
		lon2 += cid2.min_longitude;
		
	    return getDistance(lat1, lon1, lat2, lon2);
	}
	
	public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
	    double earthRadius = 6371000; //meters
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLon = Math.toRadians(lon2-lon1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLon/2) * Math.sin(dLon/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = (earthRadius * c);

	    return dist;
	}
}
