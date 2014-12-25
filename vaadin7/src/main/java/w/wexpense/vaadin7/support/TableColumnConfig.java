package w.wexpense.vaadin7.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Container;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.Table;

public class TableColumnConfig implements Serializable {

	private static final long serialVersionUID = -6938432170176885619L;

	String name;
	String header;
	Table.Align alignment;
	Converter<String, ?> converter;
	
	
	int sortOrder;
	Boolean sortAscending;
	String sortProperty;
	
	Integer width;
	Float expandRatio;
	boolean collapsed = false;
	
	
	public TableColumnConfig(String name) {
		this.name= name;
	}
	
	public TableColumnConfig(String name, String header) {
		this.name= name;
		this.header=header;
	} 
	
	public boolean isNested() {
		return name.contains(".");
	}
	
	// ===== setters =====
	
	public void setHeader(String header) {
		this.header = header;	
	}	
	
	public void setAlignment(Table.Align alignment) {
		this.alignment = alignment;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public void setSortAscending(Boolean sortAscending) {
		this.sortAscending = sortAscending;
	}

	public void setSortProperty(String sortProperty) {
		this.sortProperty = sortProperty;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public void setExpandRatio(Float expandRatio) {
		this.expandRatio = expandRatio;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}

	// ===== short methods =====

    public TableColumnConfig collapse() {
		this.collapsed=true;
		return this;
	}
	
	public void setConverter(Converter<String, ?> converter) {
        this.converter = converter;
    }

    public TableColumnConfig leftAlign() {
		this.alignment=Table.Align.LEFT;
		return this;
	}
	
	public TableColumnConfig centerAlign() {
		this.alignment=Table.Align.CENTER;
		return this;
	}
	
	public TableColumnConfig rightAlign() {
		this.alignment=Table.Align.RIGHT;
		return this;
	}
	
	public TableColumnConfig asc(int ...order ) {
		this.sortAscending=true;
		if (order!=null && order.length>0) {
			this.sortOrder = order[0];
		}
		return this;
	}
	
	public TableColumnConfig desc(int ...order) {
		this.sortAscending=false;
		if (order!=null && order.length>0) {
			this.sortOrder = order[0];
		}
		return this;
	}
	
	public TableColumnConfig sortBy(String by) {
		if (by != null && by.startsWith(".")) {		
			this.sortProperty = name + by;
		} else {
			this.sortProperty = by;
		}
		return this;		
	}
	
	public TableColumnConfig width(int width) {
		this.width=width;
		return this;
	}
	
	public TableColumnConfig expand(float ratio) {
		this.expandRatio=ratio;
		return this;
	}
		   
    public TableColumnConfig convert(Converter<String, ?> converter) {
        this.converter = converter;
        return this;
    }
	
	// ===== static methods =====
		
	public static void configure(Table table, TableColumnConfig ...configs) {
		String[] visibleColumns = new String[configs.length];
		for(int i=0;i<configs.length;i++) {
			visibleColumns[i] = configs[i].name;
		}
		
        table.setVisibleColumns(visibleColumns);
		
		for(TableColumnConfig config: configs) {
			table.setColumnCollapsed(config.name, config.collapsed);
			if (config.width != null) table.setColumnWidth(config.name, config.width);
			if (config.expandRatio != null) table.setColumnExpandRatio(config.name, config.expandRatio);
            if (config.header!=null) table.setColumnHeader(config.name,config.header);
            if (config.alignment!=null) table.setColumnAlignment(config.name, config.alignment);                    
            if (config.converter!=null) table.setConverter(config.name, config.converter); 
		}	
	}
	
	public static void sort(Container.Sortable sortable, TableColumnConfig ...configs) {
		sort(sortable, getSortConfigs(configs));
	}
	
	public static void sort(Container.Sortable sortable, Collection<TableColumnConfig> sortConfigs) {
		if (sortConfigs != null && sortConfigs.size() > 0) {			
			String[] propertyId = new String[sortConfigs.size()];
			boolean[] ascending = new boolean[sortConfigs.size()];
			int index = 0;
			for(TableColumnConfig config: sortConfigs) {
				propertyId[index] = config.name;
				ascending[index] = config.sortAscending;
				index++;
			}
			sortable.sort(propertyId, ascending);
		}			
	}
	
	public static Collection<TableColumnConfig> getSortConfigs(TableColumnConfig ...configs) {
		List<TableColumnConfig> sortConfigs = new ArrayList<TableColumnConfig>();		
		for(TableColumnConfig config: configs) {
			if (config.sortAscending != null)  sortConfigs.add(config);
		}
		Collections.sort(sortConfigs, new Comparator<TableColumnConfig>() {
			@Override
			public int compare(TableColumnConfig o1, TableColumnConfig o2) {
				return o1.sortOrder-o2.sortOrder;
			}	
		});
		return sortConfigs;
	}
	
	public static Map<String,String> getSortProperties(TableColumnConfig ...configs) {
		 Map<String,String> map = new HashMap<String,String>();
		for(TableColumnConfig config: configs) {
			if (config.sortProperty != null) { map.put(config.name, config.sortProperty); }
		}
		return map;
	}
		
	public static Collection<String> getNestedProperties(TableColumnConfig ...configs) {
		List<String> nested = new ArrayList<String>();
		for(TableColumnConfig config: configs) {
			if (config.isNested()) { nested.add(config.name); }
		}
		return nested;
	}
	

	public static TableColumnConfig parse(String name) {
		Boolean ascending = null;
		Table.Align align = null;
		boolean collapsed = false;
		Float expand = null;
		while(true) {
			if (name.charAt(0)=='(') {
				collapsed = true;
				name = name.substring(1);
				continue;
			}
			if (name.charAt(0)=='=') {
				align = Table.Align.CENTER;
				name = name.substring(1);
				continue;
			}
			if (name.charAt(0)=='<') {
				align = Table.Align.LEFT;
				name = name.substring(1);
				continue;
			}
			if (name.charAt(0)=='>') {
				align = Table.Align.RIGHT;
				name = name.substring(1);
				continue;
			}
			if (name.charAt(0)=='+') {
				ascending = true;
				name = name.substring(1);
				continue;
			}
			if (name.charAt(0)=='-') {
				ascending = false;
				name = name.substring(1);
				continue;
			}
			if (name.charAt(0)=='%') {
				expand = 1.0f;
				name = name.substring(1);
				continue;
			}
			break;
		}
		TableColumnConfig config = new TableColumnConfig(name);
		config.setCollapsed(collapsed);
		config.setAlignment(align);
		config.setSortAscending(ascending);
		config.setExpandRatio(expand);
		return config;
	}
}
