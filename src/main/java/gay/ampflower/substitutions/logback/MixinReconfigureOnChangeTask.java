package gay.ampflower.substitutions.logback;// Created 2022-21-10T10:29:36

import ch.qos.logback.classic.joran.ReconfigureOnChangeTask;
import com.oracle.svm.core.annotate.Delete;
import com.oracle.svm.core.annotate.TargetClass;

/**
 * @author Ampflower
 * @since 1.4.1
 **/
@Delete
@TargetClass(ReconfigureOnChangeTask.class)
public final class MixinReconfigureOnChangeTask {
}
