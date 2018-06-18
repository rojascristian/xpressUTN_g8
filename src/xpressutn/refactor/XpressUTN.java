package xpressutn.refactor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import xpressutn.annotations.Column;
import xpressutn.annotations.Id;
import xpressutn.annotations.ManyToOne;
import xpressutn.annotations.OneToMany;
import xpressutn.annotations.Table;
import xpressutn.utils.ConexionBD;
import xpressutn.utils.MetaData;

public class XpressUTN
{
	private static Connection conexion = ConexionBD.getConnection();

	public static <T> T find(Class<T> dtoClass, Object id)
	{
		ResultSet rs;
		try
		{
			rs=conexion.createStatement().executeQuery("select * from XPRESS.usuario");
			System.out.println("Resulset: " + rs.toString());
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T> List<T> findAll(Class<T> dtoClass)
	{
		ResultSet rs;
		try
		{
			List<String> nombresColumnas = new ArrayList<String>();
			HashMap<String, MetaData> metadataHM = new LinkedHashMap<String,MetaData>();  
			getMetaData(metadataHM, dtoClass);
			
			String queryStr = queryFindAll(metadataHM);
			
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
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
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
		    	String queryJoin = "join " + tablaSecundaria + " as " + aliasTablaSecundaria + " ON " + campoPrimario +"=" + campoSecundario;
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
		System.out.println(queryString);
		return queryString;
	}

	private static <T> void getMetaData(HashMap<String, MetaData> metadataHM, Class<T> dtoClass)
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
			getMetaData(metadataHM, entry.getValue());
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

}
