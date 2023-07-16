package gay.ampflower.substitutions.jackson;// Created 2022-21-10T11:35:44

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ext.Java7Handlers;
import com.fasterxml.jackson.databind.ext.OptionalHandlerFactory;
import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

/**
 * @author Ampflower
 * @since 1.4.1
 **/
@TargetClass(OptionalHandlerFactory.class)
public final class MixinOptionalHandlerFactory {
	@Alias
	private static Java7Handlers _jdk7Helper;

	@Substitute
	public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
		if (_jdk7Helper != null) {
			return _jdk7Helper.getSerializerForJavaNioFilePath(type.getRawClass());
		}

		return null;
	}

	@Substitute
	public JsonDeserializer<?> findDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) {
		if (_jdk7Helper != null) {
			return _jdk7Helper.getDeserializerForJavaNioFilePath(type.getRawClass());
		}

		return null;
	}

	@Substitute
	public boolean hasDeserializerFor(Class<?> valueType) {
		// jdk7Helper for some reason is left out.
		return false;
	}
}
