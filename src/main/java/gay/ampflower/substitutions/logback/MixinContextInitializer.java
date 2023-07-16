package gay.ampflower.substitutions.logback;// Created 2022-21-10T10:31:13

import ch.qos.logback.classic.util.ContextInitializer;
import com.oracle.svm.core.annotate.Delete;
import com.oracle.svm.core.annotate.TargetClass;

/**
 * @author Ampflower
 * @since 1.4.1
 **/
@TargetClass(ContextInitializer.class)
public final class MixinContextInitializer {
	@Delete
	native void autoConfig();
}
