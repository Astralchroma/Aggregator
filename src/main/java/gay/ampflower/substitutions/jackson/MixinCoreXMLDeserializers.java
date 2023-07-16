package gay.ampflower.substitutions.jackson;// Created 2022-21-10T10:55:16

import com.fasterxml.jackson.databind.ext.CoreXMLDeserializers;
import com.oracle.svm.core.annotate.Delete;
import com.oracle.svm.core.annotate.TargetClass;

/**
 * @author Ampflower
 * @since 1.4.1
 **/
@Delete
@TargetClass(CoreXMLDeserializers.class)
public final class MixinCoreXMLDeserializers {
}
