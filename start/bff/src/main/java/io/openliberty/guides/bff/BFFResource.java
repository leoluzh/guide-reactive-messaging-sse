package io.openliberty.guides.bff;

import java.util.Objects;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.openliberty.guides.models.SystemLoad;

@ApplicationScoped
@Path("/sse")
public class BFFResource {

	private Logger logger = Logger.getLogger(BFFResource.class.getName());
	
	private Sse sse;
	private SseBroadcaster broadcaster;
	
	@GET
	@Path("/")
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public void subscribeToSystem(
			@Context SseEventSink sink ,
			@Context Sse sse ) {
		
		if( Objects.isNull( this.sse ) || Objects.isNull( this.broadcaster ) ) {
			this.sse = sse ;
			this.broadcaster = sse.newBroadcaster();
		}
		
		this.broadcaster.register(sink);
		logger.info("New sink registered to broadcaster.");
		
	}
	
	private void broadcastData( String name , Object data ) {
		if( Objects.nonNull( this.broadcaster ) ) {
			OutboundSseEvent event = this.sse.newEventBuilder()
					.name(name)
					.data(data.getClass(),data)
					.mediaType(MediaType.APPLICATION_JSON_TYPE)
					.build();
			this.broadcaster.broadcast(event);
		}else {
			logger.info("Unable to send SSe. Broadcaster context is not set up.");
		}
		
	}
	
	@Incoming("systemLoad")
	public void getSystemLoadMessage( SystemLoad sl ) {
		logger.info("Message received from system.load topic " + sl.toString() );
		broadcastData("systemLoad", sl);
	}
	
}
