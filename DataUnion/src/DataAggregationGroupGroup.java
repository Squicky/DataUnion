import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;


public class DataAggregationGroupGroup {
	public long objID;
	public static long objCount = 0;

	public String edge_id_str = "";
	public Vector<String> Vector_matchedLinkNrGlobal= new Vector<String>();
	public HashMap<String, DataAggregationGroup> Groups = new HashMap<String, DataAggregationGroup>();
	
	public DataAggregationGroup SelectedDataAggregationGroup = null;
	
	public DataAggregationGroupGroup() {
		this.objID = DataAggregationGroupGroup.objCount;
		DataAggregationGroupGroup.objCount++;
	}
	
	public void aggregation() {
		for( String s : Vector_matchedLinkNrGlobal ) {
			DataAggregationGroup dag = Groups.get(s);
			dag.aggregation();
		}
		
		SelectGroup();
	}
	
	public void SelectGroup() {
		
		Comparator<Double> comparator = new Comparator<Double>() {
            @Override
            public int compare(Double a, Double b) {
                if ( a < b) {
                	return -1;
                } else {
                	return 1;
                }
            }
        };
        
        TreeMap<Double, DataAggregationGroup> temp_Group = new TreeMap<Double, DataAggregationGroup>(comparator);
        
        for( String s : Vector_matchedLinkNrGlobal ) {
			DataAggregationGroup dag = Groups.get(s);
			temp_Group.put(dag.RepresentativeValue, dag);
		}

		if (DataAggregation.GroupSelection.startsWith("med")) {
		    
	        int med_index;
	        if (temp_Group.size() % 2 == 1) {
	        	med_index = temp_Group.size() + 1;
	        	med_index = med_index / 2;
	        } else {
	        	med_index = temp_Group.size() / 2;
	        	if (DataAggregation.GroupSelection.equals("med+")) {
	        		med_index++;
	        	}
	        }
	        
			int i = 1;
			for(Map.Entry<Double, DataAggregationGroup> entry : temp_Group.entrySet()) {
				if (i == med_index) {
					SelectedDataAggregationGroup = entry.getValue();
					break;
			    }
			    i++;
			}
			
		} else if (DataAggregation.GroupSelectionDistribution != -1) {
			
			if (DataAggregation.GroupSelectionDistribution == 0) {
				SelectedDataAggregationGroup = temp_Group.firstEntry().getValue();
			} else if (DataAggregation.GroupSelectionDistribution == 1.0) {
				SelectedDataAggregationGroup = temp_Group.lastEntry().getValue();
			} else {
				double min = temp_Group.firstEntry().getValue().RepresentativeValue;
				double max = temp_Group.lastEntry().getValue().RepresentativeValue;
				
				double m = DataAggregation.GroupSelectionDistribution;
				m = m * (max - min);
				m = min + m;
				
				min = Double.MAX_VALUE;
				
				for(Map.Entry<Double, DataAggregationGroup> entry : temp_Group.entrySet()) {
					double r = entry.getValue().RepresentativeValue;
					double dif = m - r;
					if (dif < 0) {dif = dif * -1.0;}
					
					if (dif < min) {
						SelectedDataAggregationGroup = entry.getValue();
						min = dif;
				    }
				}
			}
			
		}
		
	}
}
