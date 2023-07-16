package gay.ampflower.substitutions.logback;// Created 2022-21-10T10:27:26

import ch.qos.logback.classic.LoggerContext;
import com.oracle.svm.core.annotate.Delete;
import com.oracle.svm.core.annotate.TargetClass;

/**
 * @author Ampflower
 * @since 1.4.1
 **/
@TargetClass(LoggerContext.class)
public final class MixinLoggerContext {
	@Delete
	native void reset();
}
