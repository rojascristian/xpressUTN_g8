package xpressutn.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MetaData
{
	private Class clase;
	private String nombreTabla;
	private String nombreAlias;
	private String primaryKey;
	private Field primaryKeyField;
	private List<Method> metodos;
	private List<String> primitivos;
	private List<Field> primitivosField;
	private List<Field> camposBase;
	/**
	 * key: id_(primaryKey)
	 * value: la clase
	 */
	private HashMap<String, Class> manyToOneColumns;
	private LinkedHashMap<String,Field> manyToOneColumnsField;
	private LinkedHashMap<String, Field> oneToManyColumnsField;
	private LinkedHashMap<String, Field> lazyFields;
	private HashMap<String, Field> nonLazyEntitiesColumn;
	
	public MetaData(){
		this.primitivos = new ArrayList<String>();
		this.primitivosField = new ArrayList<Field>();
		this.camposBase = new ArrayList<Field>();
		this.manyToOneColumns = new LinkedHashMap<String, Class>();
		this.manyToOneColumnsField = new LinkedHashMap<String, Field>();
		this.metodos = new ArrayList<Method>();
		this.oneToManyColumnsField = new LinkedHashMap<String, Field>();
		this.lazyFields = new LinkedHashMap<String, Field>();
		this.nonLazyEntitiesColumn = new LinkedHashMap<String, Field>();
	}
	
	public String getNombreTabla()
	{
		return nombreTabla;
	}
	public void setNombreTabla(String nombreTabla)
	{
		this.nombreTabla=nombreTabla;
	}
	public String getNombreAlias()
	{
		return nombreAlias;
	}
	public void setNombreAlias(String nombreAlias)
	{
		this.nombreAlias=nombreAlias;
	}
	public List<String> getPrimitivos()
	{
		return primitivos;
	}
	public void setPrimitivos(List<String> primitivos)
	{
		this.primitivos=primitivos;
	}
	public HashMap<String,Class> getManyToOneColumns()
	{
		return manyToOneColumns;
	}
	public void setManyToOneColumns(HashMap<String,Class> manyToOneColumns)
	{
		this.manyToOneColumns=manyToOneColumns;
	}

	public String getPrimaryKey()
	{
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey)
	{
		this.primaryKey=primaryKey;
	}

	public List<Field> getPrimitivosField()
	{
		return primitivosField;
	}

	public void setPrimitivosField(List<Field> primitivosField)
	{
		this.primitivosField=primitivosField;
	}
	
	public Integer numberOfFields(){
		return primitivosField.size() + manyToOneColumns.size();
	}

	public Class getClase()
	{
		return clase;
	}

	public void setClase(Class clase)
	{
		this.clase=clase;
	}

	public List<Method> getMetodos()
	{
		return metodos;
	}

	public void setMetodos(List<Method> metodos)
	{
		this.metodos=metodos;
	}

	public Method getSetter(Field field)
	{
		Method md = null;
		for(Method method: this.getMetodos()){
			if(method.getName().equalsIgnoreCase("set"+field.getName())){
				md = method;
			}
		}
		return md;
	}

	public LinkedHashMap<String,Field> getManyToOneColumnsField()
	{
		return manyToOneColumnsField;
	}

	public void setManyToOneColumnsField(LinkedHashMap<String,Field> manyToOneColumnsField)
	{
		this.manyToOneColumnsField=manyToOneColumnsField;
	}

	public Field getJoinFieldByIndex(int index)
	{
		return this.manyToOneColumnsField.get((this.manyToOneColumnsField.keySet().toArray())[index]);
	}

	public LinkedHashMap<String,Field> getOneToManyColumnsField()
	{
		return oneToManyColumnsField;
	}

	public void setOneToManyColumnsField(LinkedHashMap<String,Field> oneToManyColumnsField)
	{
		this.oneToManyColumnsField=oneToManyColumnsField;
	}

	public Field getPrimaryKeyField()
	{
		return primaryKeyField;
	}

	public void setPrimaryKeyField(Field primaryKeyField)
	{
		this.primaryKeyField=primaryKeyField;
	}

	public LinkedHashMap<String,Field> getLazyFields()
	{
		return lazyFields;
	}

	public void setLazyFields(LinkedHashMap<String,Field> lazyFields)
	{
		this.lazyFields=lazyFields;
	}

	public HashMap<String,Field> getNonLazyEntitiesColumn()
	{
		return nonLazyEntitiesColumn;
	}

	public void setNonLazyEntitiesColumn(HashMap<String,Field> nonLazyEntitiesColumn)
	{
		this.nonLazyEntitiesColumn=nonLazyEntitiesColumn;
	}

}
