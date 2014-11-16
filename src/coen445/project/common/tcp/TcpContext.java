package coen445.project.common.tcp;

import java.util.Collection;
import java.util.Collections;

public abstract class TcpContext {

	public Collection<? extends TcpMessage> process(BidMessage msg){
		System.out.println("Ignoring unexpected BidMessage");
				return Collections.emptySet();
	}

	public Collection<? extends TcpMessage> process(HighestMessage msg){
		System.out.println("Ignoring unexpected HighestMessage");
				return Collections.emptySet();
	}

	public Collection<? extends TcpMessage> process(WinMessage msg){
		System.out.println("Ignoring unexpected WinMessage");
				return Collections.emptySet();
	}

	public Collection<? extends TcpMessage> process(BidOverMessage msg){
		System.out.println("Ignoring unexpected BidOverMessage");
				return Collections.emptySet();
	}

	public Collection<? extends TcpMessage> process(SoldtoMessage msg){
		System.out.println("Ignoring unexpected SoldtoMessage");
				return Collections.emptySet();
	}

	public Collection<? extends TcpMessage> process(UnknownMessage msg){
		System.out.println("Ignoring unexpected UnkownMessage");
		return Collections.emptySet();
	}

}
