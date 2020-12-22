package smartform.common.redis;

import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

public class RedisTuple implements TypedTuple<String> {

	public String value;
	
	public Double score;
	
	public RedisTuple(String value, Double score)
	{
		this.value = value;
		this.score = score;
	}
	
	@Override
	public int compareTo(TypedTuple<String> o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public Double getScore() {
		// TODO Auto-generated method stub
		return score;
	}
	
	



}
