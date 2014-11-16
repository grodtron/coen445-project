package coen445.project.common.tcp;

public class TcpMessageFactory {

	private final TcpContext context;
	
	public TcpMessageFactory(TcpContext context){
		this.context = context;
	}
	
	public TcpMessage createMessage(byte [] rawdata){
		switch( TcpMessage.OpCodes.get(rawdata[0]) ){
		case BID:
			return new BidMessage(rawdata, context);
		case BID_OVER:
			return new UnknownMessage(context, rawdata); // TODO
		case HIGHEST:
			return new UnknownMessage(context, rawdata); // TODO
		case SOLDTO:
			return new UnknownMessage(context, rawdata); // TODO
		case WIN:
			return new UnknownMessage(context, rawdata); // TODO
		case UNKNOWN:
		default:
			return new UnknownMessage(context, rawdata);
		}
	}
	
}
