package org.recommender101.recommender.extensions.jfm.impl;


/**
 * This class is unused at the moment. In the original implementation it can be used to add grouping info to the input data. R101 doesn't provide info like this. The class should be functional though and is therefore kept in its current state.
 * @author Michael Jugovac (Port)
 */
public class DataMetaInfo {
        public DVectorInt attr_group  = new DVectorInt(); // attribute_id -> group_id
        public int num_attr_groups;
        public DVectorInt num_attr_per_group = new DVectorInt();

		public DataMetaInfo(int num_attributes) {
			attr_group.setSize(num_attributes);
			attr_group.init(0);
			num_attr_groups = 1;
			num_attr_per_group.setSize(num_attr_groups);
                        num_attr_per_group.init(0);
			num_attr_per_group.set(0,num_attributes);
		}
                
		public void loadGroupsFromFile(String filename) {
			throw new UnsupportedOperationException();
		}
	
		public void debug() {
			Logging.log("#attr=" + attr_group.dim + "\t#groups=" + num_attr_groups );
			for (int g = 0; g < num_attr_groups; g++) {
                            Logging.log("#attr_in_group[" + g + "]=" + num_attr_per_group.get(g));
			}
		}
    
}
