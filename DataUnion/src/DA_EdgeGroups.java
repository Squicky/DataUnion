import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;


public class DA_EdgeGroups {
	public long objID;
	public static long objCount = 0;

	public String edge_id_str = "";
	public Vector<String> Vector_matchedLinkNrGlobal= new Vector<String>();
	public HashMap<String, DA_DatasetGroup> Groups = new HashMap<String, DA_DatasetGroup>();
	
	
	public DA_DatasetGroup SelectedDataAggregationGroup = null;
	
	public DA_EdgeGroups() {
		this.objID = DA_EdgeGroups.objCount;
		DA_EdgeGroups.objCount++;
	}
	
	public void aggregation() {
		for( String s : Vector_matchedLinkNrGlobal ) {
			DA_DatasetGroup dag = Groups.get(s);
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
        
        TreeMap<Double, DA_DatasetGroup> temp_Group = new TreeMap<Double, DA_DatasetGroup>(comparator);
        
        for( String s : Vector_matchedLinkNrGlobal ) {
			DA_DatasetGroup dag = Groups.get(s);
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
			for(Map.Entry<Double, DA_DatasetGroup> entry : temp_Group.entrySet()) {
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
				
				for(Map.Entry<Double, DA_DatasetGroup> entry : temp_Group.entrySet()) {
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
