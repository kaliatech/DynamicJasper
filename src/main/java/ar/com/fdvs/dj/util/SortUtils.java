package ar.com.fdvs.dj.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ar.com.fdvs.dj.domain.entities.columns.DJExpressionColumn;
import ar.com.fdvs.dj.domain.entities.columns.DJPropertyColumn;

public class SortUtils {

	public static List sortCollection(Collection dummyCollection, List columns) {
        ArrayList l = new ArrayList(dummyCollection);
        ArrayList info = new ArrayList();
        for (Iterator iter = columns.iterator(); iter.hasNext();) {
            Object object = iter.next();
            if (object instanceof String) {
                info.add(new SortInfo((String)object, true));
            } else if (object instanceof DJExpressionColumn) {
            	//do nothing with expression columns
	        } else if (object instanceof DJPropertyColumn) {
	        	info.add(new SortInfo(((DJPropertyColumn)object).getColumnProperty().getProperty(), true));
	        }
        }
        MultiPropertyComparator mpc = new MultiPropertyComparator(info);
        Collections.sort(l, mpc);
        return l;
    }

}
