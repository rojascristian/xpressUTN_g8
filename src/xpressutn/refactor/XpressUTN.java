package xpressutn.refactor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import xpressutn.annotations.Column;
import xpressutn.annotations.Id;
import xpressutn.annotations.ManyToOne;
import xpressutn.annotations.OneToMany;
import xpressutn.annotations.Table;
import xpressutn.utils.ConexionBD;
import xpressutn.utils.MetaData;
import xpressutn.utils.PalabrasReservadasSQL;

public class XpressUTN
{
	private static Connection conexion = ConexionBD.getConnection();
	private static HashMap metadataHM = new LinkedHashMap<String, MetaData>();

	public static <T> T find(Class<T> dtoClass, Object id)
	{
//		HashMap<String, MetaData> metadataHM = new LinkedHashMap<String,MetaData>();
		metadataHM.clear();
		setMetaDataHM(metadataHM, dtoClass);  
		
		MetaData metadataEntry = getMetaDataHMByClass(metadataHM, dtoClass);
		
		String queryStr = queryFindAll(metadataHM);
		String queryWhereId = generateConditionPK(metadataEntry, (Integer)id);
		String query = queryStr + queryWhereId;
		try
		{
			printQueryResult(query);
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static String generateConditionPK(MetaData metadataEntry, Integer id)
	{
		String whereStr = "WHERE ";
		String primaryKey = metadataEntry.getNombreAlias()+"."+metadataEntry.getPrimaryKey();
		String condition = generateConditionWhere(primaryKey,PalabrasReservadasSQL.EQUALSYMBOL.getPalabraReservada(),id);
		return whereStr+condition;
	}

	private static String generateConditionWhere(String aliasPuntoPropiedad, String operador, Object valor)
	{
		
		return String.join(" ",aliasPuntoPropiedad, operador, valor.toString());
	}

	private static MetaData getMetaDataHMByClass(HashMap<String,MetaData> metadataHM, Class dtoClass)
	{
		String tableName = ((Table)dtoClass.getAnnotation(Table.class)).name();
		return metadataHM.get(formatNombreTabla(tableName));
	}

	public static <T> List<T> findAll(Class<T> dtoClass)
	{
		ResultSet rs;
		try
		{
//			HashMap<String, MetaData> metadataHM = new LinkedHashMap<String,MetaData>();
			metadataHM.clear();
			setMetaDataHM(metadataHM, dtoClass);
			
			String queryStr = queryFindAll(metadataHM);
			
			printQueryResult(queryStr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private static void printQueryResult(String queryStr) throws SQLException
	{
		ResultSet rs;
		rs=conexion.createStatement().executeQuery(queryStr);
		Integer columnsCount = rs.getMetaData().getColumnCount();
//			rs.getMetaData().getColumnName(0);
		while (rs.next()) {
			for(int index = 1; index <= columnsCount; index++){
				System.out.print(rs.getString(rs.getMetaData().getColumnName(index)) + " ");
			}
			System.out.println();
		}
	}

	private static String queryFindAll(HashMap<String,MetaData> metadataHM)
	{
		String alias = null;
		String tablaFrom = null;
		List<String> campos = new ArrayList<String>();
		List<String> joinStr = new ArrayList<String>();
		for(Entry<String,MetaData> entry : metadataHM.entrySet()) {
		    String key = entry.getKey();
		    MetaData metadataEntry = entry.getValue();
		    
		    if(tablaFrom == null){
		    	tablaFrom = metadataEntry.getNombreTabla();
		    	alias = metadataEntry.getNombreAlias();
		    }
		    
		    for(String campo: metadataEntry.getPrimitivos()){
		    	campos.add(metadataEntry.getNombreAlias() + "." + campo);
		    }
		    
		    for(Entry<String,Class> joinValues: metadataEntry.getManyToOneColumns().entrySet()){
		    	String nombreTablaSecundaria = ((Table)joinValues.getValue().getAnnotation(Table.class)).name();
		    	nombreTablaSecundaria = formatNombreTabla(nombreTablaSecundaria);
		    	MetaData claseSecundaria = metadataHM.get(nombreTablaSecundaria);
		    	String tablaSecundaria = claseSecundaria.getNombreTabla();
		    	String aliasTablaSecundaria = claseSecundaria.getNombreAlias();
		    	String campoPrimario = metadataEntry.getNombreAlias() + "." + joinValues.getKey();
		    	String campoSecundario = aliasTablaSecundaria + "." + joinValues.getKey();
		    	String queryJoin = "join " + tablaSecundaria + " as " + aliasTablaSecundaria + " ON " + campoPrimario +"=" + campoSecundario+"\n";
		    	joinStr.add(queryJoin);
		    }
		}
		
		return generateGenericQuery(tablaFrom, alias, campos, joinStr);
	}

	private static String generateGenericQuery(String tablaFrom, String alias, List<String> campos, List<String> joinStr)
	{
		String selectQuery = "SELECT " + String.join(", ",campos) + "\n";
		String fromQuery = "FROM " + tablaFrom + " " + alias + "\n";
		String joinQuery = String.join("\n",joinStr);
		String queryString = selectQuery + fromQuery + joinQuery;
		return queryString;
	}

	private static <T> void setMetaDataHM(HashMap<String, MetaData> metadataHM, Class<T> dtoClass)
	{
		MetaData clase = new MetaData();
		String tableName = dtoClass.getAnnotation(Table.class).name();
		clase.setNombreTabla(formatNombreTabla(tableName));
		clase.setNombreAlias("_"+clase.getNombreTabla());
		// OBTENGO TODOS LOS ATRIBUTOS (publicos/privados)
		final Field[] variables = dtoClass.getDeclaredFields();
		// RECORRO TODOS LOS ATRIBUTOS
		for (final Field variable : variables) {
			// APARTO LOS ATRIBUTOS CON LA ANNOTATION COLUMN
			if (variable.isAnnotationPresent(Column.class)) {	
				Annotation anotacionObtenida = variable.getAnnotation(Column.class);
				String column = (((Column)anotacionObtenida).name().equals("")) ? variable.getName() :((Column)anotacionObtenida).name(); 
				
				try {
					// ALMACENO LOS CAMPOS COLUMN
					clase.getPrimitivos().add(column);
					clase.getPrimitivosField().add(variable);
				} catch (IllegalArgumentException | SecurityException e) {
					e.printStackTrace();
				}
			}
			if (variable.isAnnotationPresent(Id.class)) {
				// TODO: hacer lo correspondiente con los atributos @ID
				Annotation anotacionObtenida = variable.getAnnotation(Column.class);
				String key = (((Column)anotacionObtenida).name().equals("")) ? "id_"+variable.getName() : ((Column)anotacionObtenida).name();
				clase.setPrimaryKey(key);
			}
			if (variable.isAnnotationPresent(ManyToOne.class)) {
				Annotation anotacionObtenida = variable.getAnnotation(ManyToOne.class);
				String key = (((ManyToOne)anotacionObtenida).columnName().equals("")) ? "id_"+variable.getName() : ((ManyToOne)anotacionObtenida).columnName();
				clase.getManyToOneColumns().put(key, variable.getType());
			}
			if (variable.isAnnotationPresent(OneToMany.class)) {
				// TODO: No me interesa, esto los manejo con cglib en momento de ejecución
			}
		}

		metadataHM.put(clase.getNombreTabla(), clase);
		
		for(Entry<String,Class> entry: clase.getManyToOneColumns().entrySet()){
			setMetaDataHM(metadataHM, entry.getValue());
		}
	}

	private static String formatNombreTabla(String tableName)
	{
		// FIX: java no toma el split directo cuando es '.'
		String[] nombreTablaArray = tableName.replace(".","-").split("-");
		String nombreTabla;
		if(nombreTablaArray.length >= 1){
			nombreTabla = nombreTablaArray[nombreTablaArray.length-1];
		} else {
			nombreTabla = tableName;
		}
		return nombreTabla;
	}
	
	// query = SELECT * FROM Usuario x WHERE x.fechaAlta > ?
	// query = SELECT * FROM Usuario x WHERE x.persona.nombre LIKE ?
	public static <T> List<T> query(Class<T> dtoClass,String query,Object args){
//		HashMap<String, MetaData> metadataHM = new LinkedHashMap<String,MetaData>();
		metadataHM.clear();
		setMetaDataHM(metadataHM, dtoClass);
		
		MetaData metadataEntry = getEntryMetadataHM(dtoClass);
		
		String queryBase = queryFindAll(metadataHM);
		String queryCondition = " WHERE " + buildConditionSQL(metadataEntry, query, args);
		String queryString = queryBase + queryCondition;
		System.out.println(queryString);
		return null;
	}

	private static <T> MetaData getEntryMetadataHM(Class<T> dtoClass)
	{
		String tableName = dtoClass.getAnnotation(Table.class).name();
		return (MetaData)metadataHM.get(formatNombreTabla(tableName));
	}

	private static <T> String buildConditionSQL(MetaData metadata, String query, Object valor)
	{
		String cadenaPropiedades = getCadenaPropiedades(query);
		List<String> joins = new ArrayList<String>();
		List<String> cadenaAliasPuntoPropiedad = new ArrayList<String>();
		String nombrePropiedad = separarCadenaPropiedades(joins, cadenaPropiedades);
		generarCadenaPropiedades(metadata, joins, nombrePropiedad, cadenaAliasPuntoPropiedad);
		String cadenaStr = String.join(".",cadenaAliasPuntoPropiedad);
		String operador = getOperadorQueryStr(query);
		return generateConditionWhere(cadenaStr,operador,valor.toString());
	}

	private static String getOperadorQueryStr(String query)
	{
		String[] queryStr = query.split("\\s");
		Integer index = searchPositionStr(queryStr, "?");
		String operador = queryStr[index-1];
		return operador;
	}

	private static void generarCadenaPropiedades(MetaData metadata, List<String> joins, String nombrePropiedad, List<String> cadenaAliasPuntoPropiedad)
	{
		// TODO Auto-generated method stub
		cadenaAliasPuntoPropiedad.add(metadata.getNombreAlias());
		if(!joins.isEmpty()){
			String claseStr = joins.remove(0);
			if(claseStr != null){
				Class claseObjeto = metadata.getManyToOneColumns().get("id_"+claseStr);
				generarCadenaPropiedades(getEntryMetadataHM(claseObjeto), joins, nombrePropiedad, cadenaAliasPuntoPropiedad);
			}
		} else {
			cadenaAliasPuntoPropiedad.add(buscarPropiedad(metadata, nombrePropiedad));
		}
	}

	private static String buscarPropiedad(MetaData metadata, String nombrePropiedad)
	{
		String columnName = null;
		for(Field propiedad: metadata.getPrimitivosField()){
			Annotation anotacionObtenida = propiedad.getAnnotation(Column.class);
			columnName = (((Column)anotacionObtenida).name().equals("")) ? propiedad.getName() :((Column)anotacionObtenida).name();
			if(columnName == nombrePropiedad){
				break;
			}
		}
		return columnName;
	}

	private static String separarCadenaPropiedades(List<String> joins, String chain)
	{
		String[] properties = chain.replace(".","-").split("-");
		String propiedad = null;
		for(int index=0;index <= properties.length - 1;index++){
			if(index>0 && index < properties.length - 1){
				joins.add(properties[index]);
			} else if(index == properties.length - 1){
				propiedad = properties[index];
			}
		}
		return propiedad;
	}

	private static String getCadenaPropiedades(String query)
	{
		String[] queryStr = query.split("\\s");
		Integer index = searchPositionStr(queryStr, PalabrasReservadasSQL.WHERE.getPalabraReservada());
		String condition = queryStr[index+1];
		return condition;
	}

	private static Integer searchPositionStr(String[] queryStr, String search)
	{
		int index = 0;
		for(String word: queryStr){
			if(word.equalsIgnoreCase(search)){
				break;
			}
			index++;
		}
		return index;
	}

}
