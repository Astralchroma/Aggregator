package gay.ampflower.substitutions;// Created 2022-24-08T16:24:39

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author Ampflower
 * @since 1.4.1
 **/
@TargetClass(className = "com.neovisionaries.ws.client.Misc")
public final class MixinMisc {
	@Alias
	@RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Reset)
	private static SecureRandom sRandom;

	@Substitute
	public static byte[] nextBytes(byte[] buffer) {
		if (sRandom == null) {
			try {
				sRandom = SecureRandom.getInstance("NativePRNGNonBlocking");
			} catch (NoSuchAlgorithmException nsae) {
				nsae.printStackTrace();
				sRandom = new SecureRandom();
			}
		}
		sRandom.nextBytes(buffer);

		return buffer;
	}
}
