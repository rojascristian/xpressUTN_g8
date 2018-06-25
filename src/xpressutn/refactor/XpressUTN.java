package xpressutn.refactor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import xpressutn.annotations.Column;
import xpressutn.annotations.Id;
import xpressutn.annotations.ManyToOne;
import xpressutn.annotations.OneToMany;
import xpressutn.annotations.Table;
import xpressutn.modelo.Persona;
import xpressutn.modelo.Usuario;
import xpressutn.utils.ConexionBD;
import xpressutn.utils.MetaData;
import xpressutn.utils.PalabrasReservadasSQL;

public class XpressUTN
{
	private static Connection conexion=ConexionBD.getConnection();
	private static HashMap metadataHM=new LinkedHashMap<String,MetaData>();

	public static <T> T find(Class<T> dtoClass, Object id)
	{
		// HashMap<String, MetaData> metadataHM = new
		// LinkedHashMap<String,MetaData>();
		T instancia=null;
		metadataHM.clear();
		setMetaDataHM(metadataHM,dtoClass);

		MetaData metadataEntry=getMetaDataHMByClass(metadataHM,dtoClass);

		String queryStr=queryFindAll(metadataHM);
		String queryWhereId=generateConditionPK(metadataEntry,(Integer)id);
		String query=queryStr+queryWhereId;
		try
		{
			ResultSet resultSet=printQueryResult(query);

			instancia=(T)getFirst(resultSet);

			instancia = createProxy(instancia, metadataEntry.getOneToManyColumnsField());
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(instancia);
		return instancia;
	}

	private static <T> T createProxy(T instancia, HashMap<String, Field> oneToManyFields)
	{
		Enhancer enhancer=new Enhancer();
		enhancer.setSuperclass(instancia.getClass());
		enhancer.setCallback(new InvocationHandler()
		{
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				if(oneToManyFields.containsKey(method.getName().toLowerCase())){
//					System.out.println(method.getName());
					Field field = oneToManyFields.get(method.getName().toLowerCase());
					Class clase = getClassParametized(field);

					String xql = generateQuerySuperPowa(method);
//					System.out.println(xql);
					method.invoke(instancia, query(clase, xql, 1));
				}
				return null;
			}

			private String generateQuerySuperPowa(Method method)
			{
				Field field = oneToManyFields.get(method.getName().toLowerCase());
				Class clase = getClassParametized(field);
				Annotation anotacionObtenida=field.getAnnotation(OneToMany.class);
				String queryStr = "SELECT * FROM "+clase.getSimpleName()+" WHERE x."+((OneToMany)anotacionObtenida).mappedBy()+"= ?"; 
				return queryStr;
			}

			private Class getClassParametized(Field field)
			{
			    ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
		        Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
				return stringListClass;
			}

		});
		return (T)enhancer.create();
	}

	private static String generateConditionPK(MetaData metadataEntry, Integer id)
	{
		String whereStr="WHERE ";
		String primaryKey=metadataEntry.getNombreAlias()+"."+metadataEntry.getPrimaryKey();
		String condition=generateConditionWhere(primaryKey,PalabrasReservadasSQL.EQUALSYMBOL.getPalabraReservada(),id);
		return whereStr+condition;
	}

	private static String generateConditionWhere(String aliasPuntoPropiedad, String operador, Object valor)
	{

		return String.join(" ",aliasPuntoPropiedad,operador,valor.toString());
	}

	private static MetaData getMetaDataHMByClass(HashMap<String,MetaData> metadataHM, Class dtoClass)
	{
		String tableName=((Table)dtoClass.getAnnotation(Table.class)).name();
		return metadataHM.get(formatNombreTabla(tableName));
	}

	public static <T> List<T> findAll(Class<T> dtoClass)
	{
		ResultSet rs;
		List resultSetObject=null;
		try
		{
			// HashMap<String, MetaData> metadataHM = new
			// LinkedHashMap<String,MetaData>();
			metadataHM.clear();
			setMetaDataHM(metadataHM,dtoClass);

			String queryStr=queryFindAll(metadataHM);
			ResultSet resultSet=printQueryResult(queryStr);

			resultSetObject=getAllResults(resultSet);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return resultSetObject;
	}

	private static List getAllResults(ResultSet resultSet)
	{
		return transformResultSetToObjectList(resultSet);
	}

	private static Object getFirst(ResultSet resultSet) throws SQLException
	{
		List resultSetList=transformResultSetToObjectList(resultSet);
		return resultSetList.get(0);
	}

	private static Object getFirstWithLocalHM(ResultSet resultSet, HashMap<String,MetaData> metaDataLocalHM) throws SQLException
	{
		List resultSetList=transformResultSetToObjectList(resultSet,metaDataLocalHM);
		return resultSetList.get(0);
	}

	private static List transformResultSetToObjectList(ResultSet resultSet, HashMap<String,MetaData> metaDataLocalHM)
	{
		Object instancia=null;
		MetaData md=null;
		List resultados=new ArrayList();
		int i=0;
		for(Entry<String,MetaData> entry:metaDataLocalHM.entrySet())
		{
			md=entry.getValue();
			try
			{
				while(resultSet.next())
				{
					instancia=getInstance(md.getClase());
					for(int index=1; index<=md.getPrimitivosField().size()+md.getManyToOneColumns().size(); index++)
					{
						if(index<=md.getPrimitivosField().size())
						{
							Method metodo=md.getSetter(md.getPrimitivosField().get(index-1));
							metodo.invoke(instancia,resultSet.getObject(index));
						}
						else
						{
							Field fd=md.getJoinFieldByIndex(index-md.getPrimitivosField().size()-1);
							Method metodo=md.getSetter(fd);
							metodo.invoke(instancia,findWithLocalHM(md.getManyToOneColumns().get("id_"+fd.getName()),resultSet.getObject(index)));
						}
					}
					resultados.add(instancia);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return resultados;
	}

	private static List transformResultSetToObjectList(ResultSet resultSet)
	{
		Object instancia=null;
		MetaData md=null;
		List resultados=new ArrayList();
		int i=0;
		for(Entry<String,MetaData> entry:((HashMap<String,MetaData>)metadataHM).entrySet())
		{
			md=entry.getValue();
			try
			{
				while(resultSet.next())
				{
					instancia=getInstance(md.getClase());
					for(int index=1; index<=md.getPrimitivosField().size()+md.getManyToOneColumns().size(); index++)
					{
						if(index<=md.getPrimitivosField().size())
						{
							Method metodo=md.getSetter(md.getPrimitivosField().get(index-1));
							metodo.invoke(instancia,resultSet.getObject(index));
						}
						else
						{
							Field fd=md.getJoinFieldByIndex(index-md.getPrimitivosField().size()-1);
							Method metodo=md.getSetter(fd);
							Persona p=findWithLocalHM(md.getManyToOneColumns().get("id_"+fd.getName()),resultSet.getObject(index));
							metodo.invoke(instancia,p);
						}
					}
					resultados.add(instancia);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}
		return resultados;
	}

	private static <T> T findWithLocalHM(Class class1, Object id)
	{
		HashMap metadataHMLocal=new LinkedHashMap<String,MetaData>();

		setMetaDataHM(metadataHMLocal,class1);

		MetaData metadataEntry=getMetaDataHMByClass(metadataHMLocal,class1);

		String queryStr=queryFindAll(metadataHMLocal);
		String queryWhereId=generateConditionPK(metadataEntry,(Integer)id);
		String query=queryStr+queryWhereId;
		T instanciaLocal=null;
		try
		{
			ResultSet resultSet=printQueryResult(query);

			instanciaLocal=(T)getFirstWithLocalHM(resultSet,metadataHMLocal);
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instanciaLocal;
	}

	private static <T> Object getInstance(Class<T> clase)
			throws NoSuchMethodException,SecurityException,InstantiationException,IllegalAccessException,IllegalArgumentException,InvocationTargetException
	{
		Constructor<T> constructorDtoClass=clase.getConstructor();
		return constructorDtoClass.newInstance();
	}

	private static ResultSet printQueryResult(String queryStr) throws SQLException
	{
		ResultSet rs;
		rs=conexion.createStatement().executeQuery(queryStr);
		// Integer columnsCount = rs.getMetaData().getColumnCount();
		// while (rs.next()) {
		// for(int index = 1; index <= columnsCount; index++){
		// System.out.print(rs.getString(rs.getMetaData().getColumnName(index))
		// + " ");
		// }
		// System.out.println();
		// }
		return rs;
	}

	private static String queryFindAll(HashMap<String,MetaData> metadataHM)
	{
		String alias=null;
		String tablaFrom=null;
		List<String> campos=new ArrayList<String>();
		List<String> joinStr=new ArrayList<String>();
		for(Entry<String,MetaData> entry:metadataHM.entrySet())
		{
			String key=entry.getKey();
			MetaData metadataEntry=entry.getValue();

			if(tablaFrom==null)
			{
				tablaFrom=metadataEntry.getNombreTabla();
				alias=metadataEntry.getNombreAlias();
			}

			for(String campo:metadataEntry.getPrimitivos())
			{
				campos.add(metadataEntry.getNombreAlias()+"."+campo);
			}

			for(Entry<String,Class> joinValues:metadataEntry.getManyToOneColumns().entrySet())
			{
				String nombreTablaSecundaria=((Table)joinValues.getValue().getAnnotation(Table.class)).name();
				nombreTablaSecundaria=formatNombreTabla(nombreTablaSecundaria);
				MetaData claseSecundaria=metadataHM.get(nombreTablaSecundaria);
				String tablaSecundaria=claseSecundaria.getNombreTabla();
				String aliasTablaSecundaria=claseSecundaria.getNombreAlias();
				String campoPrimario=metadataEntry.getNombreAlias()+"."+joinValues.getKey();
				String campoSecundario=aliasTablaSecundaria+"."+joinValues.getKey();
				String queryJoin="join "+tablaSecundaria+" as "+aliasTablaSecundaria+" ON "+campoPrimario+"="+campoSecundario+"\n";
				joinStr.add(queryJoin);
				campos.add(campoPrimario);
			}
		}

		return generateGenericQuery(tablaFrom,alias,campos,joinStr);
	}

	private static String generateGenericQuery(String tablaFrom, String alias, List<String> campos, List<String> joinStr)
	{
		String selectQuery="SELECT "+String.join(", ",campos)+"\n";
		String fromQuery="FROM "+tablaFrom+" "+alias+"\n";
		String joinQuery=String.join("\n",joinStr);
		String queryString=selectQuery+fromQuery+joinQuery;
		return queryString;
	}

	private static <T> void setMetaDataHM(HashMap<String,MetaData> metadataHM, Class<T> dtoClass)
	{
		MetaData clase=new MetaData();
		String tableName=dtoClass.getAnnotation(Table.class).name();
		clase.setNombreTabla(formatNombreTabla(tableName));
		clase.setNombreAlias("_"+clase.getNombreTabla());
		clase.setClase(dtoClass);
		for(Method method:dtoClass.getMethods())
		{
			clase.getMetodos().add(method);
		}
		// OBTENGO TODOS LOS ATRIBUTOS (publicos/privados)
		final Field[] variables=dtoClass.getDeclaredFields();
		// RECORRO TODOS LOS ATRIBUTOS
		for(final Field variable:variables)
		{
			// APARTO LOS ATRIBUTOS CON LA ANNOTATION COLUMN
			if(variable.isAnnotationPresent(Column.class))
			{
				Annotation anotacionObtenida=variable.getAnnotation(Column.class);
				String column=(((Column)anotacionObtenida).name().equals(""))?variable.getName():((Column)anotacionObtenida).name();

				try
				{
					// ALMACENO LOS CAMPOS COLUMN
					clase.getPrimitivos().add(column);
					clase.getPrimitivosField().add(variable);
				}
				catch(IllegalArgumentException|SecurityException e)
				{
					e.printStackTrace();
				}
			}
			if(variable.isAnnotationPresent(Id.class))
			{
				// TODO: hacer lo correspondiente con los atributos @ID
				Annotation anotacionObtenida=variable.getAnnotation(Column.class);
				String key=(((Column)anotacionObtenida).name().equals(""))?"id_"+variable.getName():((Column)anotacionObtenida).name();
				clase.setPrimaryKey(key);
				clase.setPrimaryKeyField(variable);
			}
			if(variable.isAnnotationPresent(ManyToOne.class))
			{
				Annotation anotacionObtenida=variable.getAnnotation(ManyToOne.class);
				String key=(((ManyToOne)anotacionObtenida).columnName().equals(""))?"id_"+variable.getName():((ManyToOne)anotacionObtenida).columnName();
				clase.getManyToOneColumns().put(key,variable.getType());
				clase.getManyToOneColumnsField().put(key,variable);
			}
			if(variable.isAnnotationPresent(OneToMany.class))
			{
				// TODO: No me interesa, esto los manejo con cglib en momento de
				// ejecución
//			    ParameterizedType stringListType = (ParameterizedType) variable.getGenericType();
//		        Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
		        clase.getOneToManyColumnsField().put("get"+variable.getName().toLowerCase(), variable);
			}
		}

		metadataHM.put(clase.getNombreTabla(),clase);

		for(Entry<String,Class> entry:clase.getManyToOneColumns().entrySet())
		{
			setMetaDataHM(metadataHM,entry.getValue());
		}
	}

	private static String formatNombreTabla(String tableName)
	{
		// FIX: java no toma el split directo cuando es '.'
		String[] nombreTablaArray=tableName.replace(".","-").split("-");
		String nombreTabla;
		if(nombreTablaArray.length>=1)
		{
			nombreTabla=nombreTablaArray[nombreTablaArray.length-1];
		}
		else
		{
			nombreTabla=tableName;
		}
		return nombreTabla;
	}

	// query = SELECT * FROM Usuario x WHERE x.fechaAlta > ?
	// query = SELECT * FROM Usuario x WHERE x.persona.nombre LIKE ?
	public static <T> List<T> query(Class<T> dtoClass, String query, Object args)
	{
		metadataHM.clear();
		setMetaDataHM(metadataHM,dtoClass);

		MetaData metadataEntry=getEntryMetadataHM(dtoClass);

		String queryBase=queryFindAll(metadataHM);
		String queryCondition=" WHERE "+buildConditionSQL(metadataEntry,query,args);
		String queryString=queryBase+queryCondition;

		System.out.println(queryString);
		
		try
		{
			printQueryResult(queryString);
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private static <T> MetaData getEntryMetadataHM(Class<T> dtoClass)
	{
		String tableName=dtoClass.getAnnotation(Table.class).name();
		return (MetaData)metadataHM.get(formatNombreTabla(tableName));
	}

	private static <T> String buildConditionSQL(MetaData metadata, String query, Object valor)
	{
		String cadenaPropiedades=getCadenaPropiedades(query);
		List<String> joins=new ArrayList<String>();
		List<String> cadenaAliasPuntoPropiedad=new ArrayList<String>();
		String nombrePropiedad=separarCadenaPropiedades(joins,cadenaPropiedades);
		generarCadenaPropiedades(metadata,joins,nombrePropiedad,cadenaAliasPuntoPropiedad);
		cadenaAliasPuntoPropiedad.remove(0);
		String cadenaStr=String.join(".",cadenaAliasPuntoPropiedad);
		String operador=getOperadorQueryStr(query);
		return generateConditionWhere(cadenaStr,operador,valor.toString());
	}

	private static String getOperadorQueryStr(String query)
	{
		String[] queryStr=query.split("\\s");
		Integer index=searchPositionStr(queryStr,"?");
		String operador=queryStr[index-1];
		return operador;
	}

	private static void generarCadenaPropiedades(MetaData metadata, List<String> joins, String nombrePropiedad, List<String> cadenaAliasPuntoPropiedad)
	{
		// TODO Auto-generated method stub
		cadenaAliasPuntoPropiedad.add(metadata.getNombreAlias());
		if(!joins.isEmpty())
		{
			String claseStr=joins.remove(0);
			if(claseStr!=null)
			{
				Class claseObjeto=metadata.getManyToOneColumns().get("id_"+claseStr);
				generarCadenaPropiedades(getEntryMetadataHM(claseObjeto),joins,nombrePropiedad,cadenaAliasPuntoPropiedad);
			}
		}
		else
		{
			cadenaAliasPuntoPropiedad.add(buscarPropiedad(metadata,nombrePropiedad));
		}
	}

	private static String buscarPropiedad(MetaData metadata, String nombrePropiedad)
	{
		String columnName=null;
		for(Field propiedad:metadata.getPrimitivosField())
		{
			Annotation anotacionObtenida=propiedad.getAnnotation(Column.class);
			columnName=(((Column)anotacionObtenida).name().equals(""))?propiedad.getName():((Column)anotacionObtenida).name();
			if(columnName.equals(nombrePropiedad))
			{
				break;
			}
		}
		return columnName;
	}

	private static String separarCadenaPropiedades(List<String> joins, String chain)
	{
		String[] properties=chain.replace(".","-").split("-");
		String propiedad=null;
		for(int index=0; index<=properties.length-1; index++)
		{
			if(index>0&&index<properties.length-1)
			{
				joins.add(properties[index]);
			}
			else if(index==properties.length-1)
			{
				propiedad=properties[index];
			}
		}
		return propiedad;
	}

	private static String getCadenaPropiedades(String query)
	{
		String[] queryStr=query.split("\\s");
		Integer index=searchPositionStr(queryStr,PalabrasReservadasSQL.WHERE.getPalabraReservada());
		String condition=queryStr[index+1];
		return condition;
	}

	private static Integer searchPositionStr(String[] queryStr, String search)
	{
		int index=0;
		for(String word:queryStr)
		{
			if(word.equalsIgnoreCase(search))
			{
				break;
			}
			index++;
		}
		return index;
	}

	public static <T> List<T> queryForSingleRow(Class<T> dtoClass, String query, Object args)
	{
		// HashMap<String, MetaData> metadataHM = new
		// LinkedHashMap<String,MetaData>();
		metadataHM.clear();
		setMetaDataHM(metadataHM,dtoClass);

		MetaData metadataEntry=getEntryMetadataHM(dtoClass);

		String queryBase=queryFindAll(metadataHM);
		String queryCondition=" WHERE "+buildConditionSQL(metadataEntry,query,args);
		String queryString=queryBase+queryCondition;
		return null;
	}

}
