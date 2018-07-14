package xpressutn.refactor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import xpressutn.annotations.OneToMany;

public class Interceptor implements MethodInterceptor
{

	private Object target;
	LinkedHashMap<String,Field> oneToManyColumnsField;

	public Interceptor(Object target, LinkedHashMap<String,Field> oneToManyColumnsField){
		this.target=target;
		this.oneToManyColumnsField = oneToManyColumnsField;
	}
	
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable
	{
		if(this.oneToManyColumnsField.containsKey(method.getName().toLowerCase()))
		{
			Field field=this.oneToManyColumnsField.get(method.getName().toLowerCase());
			Class clase=getClassParametized(field);

			String xql=generateQuerySuperPowa(method);
			System.out.println(xql);
			method.invoke(obj,XpressUTN.query(clase,xql,1));
			System.out.println("método del proxy.");
			return null;
		}
		System.out.println("método de la clase.");
		return proxy.invoke(target,args);
	}
	
	private String generateQuerySuperPowa(Method method)
	{
		Field field=this.oneToManyColumnsField.get(method.getName().toLowerCase());
		Class clase=getClassParametized(field);
		Annotation anotacionObtenida=field.getAnnotation(OneToMany.class);
		String queryStr="SELECT * FROM "+clase.getSimpleName()+" WHERE x."+((OneToMany)anotacionObtenida).mappedBy()+"= ?";
		return queryStr;
	}

	private Class getClassParametized(Field field)
	{
		ParameterizedType stringListType=(ParameterizedType)field.getGenericType();
		Class<?> stringListClass=(Class<?>)stringListType.getActualTypeArguments()[0];
		return stringListClass;
	}


}
