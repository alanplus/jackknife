package com.lwh.jackknife.multiproxy.annotation.handler;

import com.lwh.jackknife.multiproxy.annotation.Wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * 专门用来处理{@link Wrapper}的处理器。
 */
public class WrapperHandler implements AnnotationHandler {

    private ProcessingEnvironment processingEnv;

    @Override
    public void attachProcessingEnvironment(ProcessingEnvironment env) {
        this.processingEnv = env;
    }

    @Override
    public Map<String, List<Element>> handleAnnotation(RoundEnvironment env) {
        Map<String, List<Element>> annotationMap = new HashMap<>();
        Set<? extends Element> elementSet = env.getElementsAnnotatedWith(Wrapper.class);
        for (Element element : elementSet) {
            TypeElement typeElement = (TypeElement) element;
            String packageName = getPackageName(processingEnv, typeElement);
            String className = packageName + "." + typeElement.getSimpleName().toString();
            List<Element> cacheElements = annotationMap.get(className);
            if (cacheElements == null) {
                cacheElements = new ArrayList<>();
                annotationMap.put(className, cacheElements);
            }
            cacheElements.add(typeElement);
        }
        return annotationMap;
    }

    private String getPackageName(ProcessingEnvironment env, Element element) {
        return env.getElementUtils().getPackageOf(element).getQualifiedName().toString();
    }
}