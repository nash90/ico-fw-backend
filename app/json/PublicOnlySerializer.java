package json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class PublicOnlySerializer
    extends
        BaseEntitySerializer<BaseEntity>
{

	// --- STATIC FIELDS --- //

	private static ObjectMapper MAPPER = makeObjectMapper();

	// --- STATIC METHODS --- //

	private static ObjectMapper makeObjectMapper()
	{
		SimpleModule sm = new SimpleModule();
		sm.addSerializer(new PublicOnlySerializer());

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(sm);

		return mapper;
	}

	public static JsonNode serialize(
	    Object obj)
	{
		return MAPPER.valueToTree(obj);
	}

	// --- CONSTRUCTORS --- //

	public PublicOnlySerializer()
	{
		super(BaseEntity.class, true);
	}

}
