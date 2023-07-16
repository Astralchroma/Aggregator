package gay.ampflower.substitutions.jackson;// Created 2022-21-10T10:54:36

import com.fasterxml.jackson.databind.ext.DOMSerializer;
import com.oracle.svm.core.annotate.Delete;
import com.oracle.svm.core.annotate.TargetClass;

/**
 * @author Ampflower
 * @since 1.4.1
 **/
@Delete
@TargetClass(DOMSerializer.class)
public final class MixinDOMSerializer {
}
