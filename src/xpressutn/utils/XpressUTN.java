package xpressutn.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import xpressutn.annotations.Column;
import xpressutn.annotations.Id;
import xpressutn.annotations.ManyToOne;
import xpressutn.annotations.OneToMany;
import xpressutn.annotations.Table;

public class XpressUTN
{
	public String findAll(Class clase){
		List<String> nombresColumnas = new ArrayList<String>();
		
		// OBTENGO TODOS LOS ATRIBUTOS (publicos/privados)
		final Field[] variables = clase.getDeclaredFields();
		// RECORRO TODOS LOS ATRIBUTOS
		for (final Field variable : variables) {
			// APARTO LOS ATRIBUTOS CON LA ANNOTATION COLUMN
			if (variable.isAnnotationPresent(Column.class)) {	
				Annotation anotacionObtenida = variable.getAnnotation(Column.class);
				try {
					// ALMACENO LOS CAMPOS COLUMN
					nombresColumnas.add(((Column)anotacionObtenida).name());
				} catch (IllegalArgumentException | SecurityException e) {
					e.printStackTrace();
				}
			}
			if (variable.isAnnotationPresent(Id.class)) {
				// TODO: hacer lo correspondiente con los atributos @ID
			}
			if (variable.isAnnotationPresent(ManyToOne.class)) {
				// TODO: hacer lo correspondiente con los atributos @ManyToOne
			}
			if (variable.isAnnotationPresent(OneToMany.class)) {
				// TODO: hacer lo correspondiente con los atributos @ManyToOne
			}
		}
		
		final Annotation annotationTabla = clase.getAnnotation(Table.class);
		
		return queryFindAll(nombresColumnas, ((Table)annotationTabla).name());
	}

	private String queryFindAll(List<String> nombresColumnas, String claseNombre)
	{
		String nombresColumnasSeparadosComas = nombresColumnas.toString().replace("[", "").replace("]", "");
		String query = "SELECT " + nombresColumnasSeparadosComas + " FROM " + claseNombre;
		return query;
	}

	public void executePlainQuery(String query) throws SQLException
	{
	    Properties prop = new Properties();
	    InputStream input = null;

	    try {

	        input = new FileInputStream("db.properties");

	        // load a properties file
	        prop.load(input);
	        
	        String conexion_url = prop.getProperty("jdbc.connection.url");
	        String usuario_conexion = prop.getProperty("jdbc.connection.user");
	        String usuario_password = prop.getProperty("jdbc.connection.password");

	        // get the property value and print it out
	        Connection con = DriverManager.getConnection(conexion_url,usuario_conexion,usuario_password);
	        ResultSet rs = con.createStatement().executeQuery(query);
	        while (rs.next()) {
	            int idPersona = rs.getInt("id_persona");
	            String nombre = rs.getString("nombre");
	            System.out.println(idPersona + "\t" + nombre);
	        }

	    } catch (IOException ex) {
	        ex.printStackTrace();
	    } finally {
	        if (input != null) {
	            try {
	                input.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}
}
