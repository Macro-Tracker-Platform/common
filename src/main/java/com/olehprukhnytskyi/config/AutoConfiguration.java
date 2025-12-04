package com.olehprukhnytskyi.config;

import com.olehprukhnytskyi.config.aop.IdempotencyAspect;
import com.olehprukhnytskyi.controller.FaviconController;
import com.olehprukhnytskyi.exception.GlobalExceptionHandler;
import com.olehprukhnytskyi.model.CommonEntityMarker;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EntityScan(basePackageClasses = CommonEntityMarker.class)
@Import({
		SwaggerConfig.class,
		IdempotencyAspect.class,
		FaviconController.class,
		GlobalExceptionHandler.class
})
public class AutoConfiguration {
}
