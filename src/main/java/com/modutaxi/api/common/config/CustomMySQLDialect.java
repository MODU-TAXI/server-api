package com.modutaxi.api.common.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.query.sqm.produce.function.StandardFunctionArgumentTypeResolvers;

public class CustomMySQLDialect extends MySQLDialect {
    public CustomMySQLDialect() {
        super();
    }

    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);
        bitAndFunctionRegistry(functionContributions);
    }

    private void bitAndFunctionRegistry(FunctionContributions functionContributions) {
        final String FUNCTION_NAME = "BIT_AND";
        final String FUNCTION_PATTERN = "?1 & ?2";
        functionContributions.getFunctionRegistry()
            .patternDescriptorBuilder(FUNCTION_NAME, FUNCTION_PATTERN)
            .setExactArgumentCount(2)
            .setArgumentTypeResolver(StandardFunctionArgumentTypeResolvers.ARGUMENT_OR_IMPLIED_RESULT_TYPE)
            .register();
    }

}
