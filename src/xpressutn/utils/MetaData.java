package xpressutn.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MetaData
{
	private String nombreTabla;
	private String nombreAlias;
	private String primaryKey;
	private List<String> primitivos;
	/**
	 * key: id_(primaryKey)
	 * value: la clase
	 */
	private HashMap<String, Class> manyToOneColumns;
	
	public MetaData(){
		this.primitivos = new ArrayList<String>();
		this.manyToOneColumns = new HashMap<String, Class>();
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

}
