package xpressutn.refactor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import xpressutn.annotations.ManyToOne;
import xpressutn.annotations.OneToMany;
import xpressutn.utils.MetaData;

public class Interceptor implements MethodInterceptor
{

	private Object target;
	MetaData metaData;
	private LinkedHashMap<String,MetaData> metaDataHM;

	public Interceptor(Object target, MetaData metadataEntry, LinkedHashMap<String,MetaData> metadataHM){
		this.target=target;
		this.metaData = metadataEntry;
		this.metaDataHM = metadataHM;
	}
	
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable
	{
		if(this.metaData.getLazyFields().containsKey(method.getName().toLowerCase()))
//			if(this.metaData.getOneToManyColumnsField().containsKey(method.getName().toLowerCase()))
		{
			System.out.println("método del proxy.\n");
//			Field field=this.metaData.getOneToManyColumnsField().get(method.getName().toLowerCase());
			Field field=this.metaData.getLazyFields().get(method.getName().toLowerCase());
			Class clase=getClassParametized(field);

			String xql=generateQuerySuperPowa(method);
			System.out.println("PSEUDO QUERY GENERADA QUE SE LE PASA AL MÉTODO QUERY DE XPRESS");
			System.out.println(xql+"\n");
			
			Method setterMethod = getMethodSetter(method);
			Method getterMethodPK = getMethodPK();
			if(field.isAnnotationPresent(OneToMany.class)){
				return XpressUTN.query(clase,xql,getterMethodPK.invoke(obj));
			} else if(field.isAnnotationPresent(ManyToOne.class)){
//				TODO: OBTENER LA SENTENCIA SQL QUE TENGA TODOS LOS CAMPOS(sean o no lazy)
				String str = XpressUTN.generateExplicitQuery(getterMethodPK.invoke(obj),this.metaDataHM, this.metaData);
				System.out.println(str);
//				XpressUTN.query(this.metaData.getClase(),xql,getterMethodPK.invoke(obj));
			}
//			setterMethod.invoke(obj,objectList);
			return null;
		}
		System.out.println("método de la clase.");
		System.out.println(method.getName());
		return proxy.invoke(target,args);
	}
	
	private Method getMethodPK()
	{
		Method setterMethod = null;
		Field fieldPK = metaData.getPrimaryKeyField();
		for(Method metodo: this.metaData.getMetodos()){
			if(metodo.getName().equalsIgnoreCase("get"+fieldPK.getName())){
				setterMethod = metodo;
				break;
			}
		}
		return setterMethod;
	}

	private Method getMethodSetter(Method method)
	{
		String strMethod = method.getName().substring(3,method.getName().length());
		Method getterMethod = null;
		for(Method metodo: this.metaData.getMetodos()){
			if(metodo.getName().equalsIgnoreCase("set"+strMethod)){
				getterMethod = metodo;
				break;
			}
		}
		return getterMethod;
	}

	private String generateQuerySuperPowa(Method method)
	{
//		Field field=this.metaData.getOneToManyColumnsField().get(method.getName().toLowerCase());
		Field field=this.metaData.getLazyFields().get(method.getName().toLowerCase());
		Class clase=getClassParametized(field);
		String queryStr = null;
		if(field.isAnnotationPresent(OneToMany.class)){
			Annotation anotacionObtenida=field.getAnnotation(OneToMany.class);
			queryStr="SELECT * FROM "+clase.getSimpleName()+" x WHERE x."+((OneToMany)anotacionObtenida).mappedBy()+" = ?";
		}
		if(field.isAnnotationPresent(ManyToOne.class)){
			queryStr="SELECT * FROM "+this.metaData.getClase().getName()+" x WHERE x."+this.metaData.getPrimaryKeyField().getName()+" = ?";			
		}
		return queryStr;
	}

	private Class getClassParametized(Field field)
	{
		if(field.isAnnotationPresent(ManyToOne.class)){
			return field.getType();
		}
		if(field.isAnnotationPresent(OneToMany.class)){
			ParameterizedType stringListType=(ParameterizedType)field.getGenericType();
			Class<?> stringListClass=(Class<?>)stringListType.getActualTypeArguments()[0];
			return stringListClass;
		}
		return null;
	}


}
