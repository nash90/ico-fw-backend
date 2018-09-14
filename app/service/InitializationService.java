
package service;

import java.util.Map;
import com.google.inject.ImplementedBy;
import serviceImpl.InitializationServiceImpl;

@ImplementedBy(InitializationServiceImpl.class)
public interface InitializationService
{

	public String init();

	public String storeKey(
	    Map<String, String[]> formData);

	public String handleInitialization(
	    Map<String, String[]> formData);
}
