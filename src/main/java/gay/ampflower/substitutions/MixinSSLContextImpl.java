package gay.ampflower.substitutions;// Created 2022-24-08T15:19:41

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
@TargetClass(className = "sun.security.ssl.SSLContextImpl")
public final class MixinSSLContextImpl {
	@Alias
	@RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Reset)
	private SecureRandom secureRandom;

	@Substitute
	public SecureRandom getSecureRandom() {
		if (secureRandom == null) {
			try {
				secureRandom = SecureRandom.getInstance("NativePRNGNonBlocking");
			} catch (NoSuchAlgorithmException nsae) {
				nsae.printStackTrace();
				secureRandom = new SecureRandom();
			}
		}
		return secureRandom;
	}
}
