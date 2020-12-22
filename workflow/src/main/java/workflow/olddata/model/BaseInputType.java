package workflow.olddata.model;

import graphql.schema.GraphQLInputType;

/**
 * 在graphql中,Resolver定义Mutation类型的方法时，参数必须继承该类
 */
public class BaseInputType implements GraphQLInputType{

	@Override
	public String getName() {
		return null;
	}

}
