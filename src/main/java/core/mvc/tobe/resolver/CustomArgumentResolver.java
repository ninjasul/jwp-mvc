package core.mvc.tobe.resolver;

import core.mvc.tobe.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class CustomArgumentResolver extends AbstractHandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return !isHttpServletRequestType(parameter.getType()) &&
            !isHttpServletResponseType(parameter.getType()) &&
            !isPrimitiveOrWrapperType(parameter.getType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, HttpServletRequest request, HttpServletResponse response) {
        return getCustomObjectArgument(request, parameter.getType());
    }

    private Object getCustomObjectArgument(HttpServletRequest request, Class<?> type) {
        try {
            Object[] parameters = resolveParameters(request, type.getDeclaredFields());
            return ReflectionUtils.newInstance(type, parameters);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public Object[] resolveParameters(HttpServletRequest request, Field[] fields) {
        if (ArrayUtils.isEmpty(fields)) {
            return null;
        }

        return Arrays.stream(fields)
            .map(field -> resolveArgument(request.getParameter(field.getName()), field.getType()))
            .toArray(Object[]::new);
    }
}
