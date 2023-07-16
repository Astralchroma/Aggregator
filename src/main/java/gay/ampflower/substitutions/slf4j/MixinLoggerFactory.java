package gay.ampflower.substitutions.slf4j;// Created 2022-21-10T10:32:41

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Delete;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.SLF4JServiceProvider;

/**
 * @author Ampflower
 * @since 1.4.1
 **/
@TargetClass(LoggerFactory.class)
public final class MixinLoggerFactory {
	@Alias
	static volatile SLF4JServiceProvider PROVIDER;

	@Delete
	static native void reset();

	@Substitute
	static SLF4JServiceProvider getProvider() {
		return PROVIDER;
	}
}
