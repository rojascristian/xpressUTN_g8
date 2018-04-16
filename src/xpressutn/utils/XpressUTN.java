package xpressutn.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

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
}
