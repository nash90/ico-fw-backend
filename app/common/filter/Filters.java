package common.filter;

import javax.inject.Inject;
import play.filters.gzip.GzipFilter;
import play.http.DefaultHttpFilters;

public class Filters
    extends
        DefaultHttpFilters
{
	@Inject
	public Filters(
	               GzipFilter gzip,
	               CustomFilter customFilter)
	{
		super(gzip, customFilter);
	}
}
