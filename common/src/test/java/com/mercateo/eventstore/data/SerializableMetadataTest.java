package com.mercateo.eventstore.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.mercateo.common.UnitTest;
import com.mercateo.eventstore.domain.Causality;
import com.mercateo.eventstore.domain.EventId;
import com.mercateo.eventstore.domain.EventInitiator;
import com.mercateo.eventstore.domain.EventType;
import com.mercateo.eventstore.json.EventJsonMapper;

import lombok.val;

@Category(UnitTest.class)
public class SerializableMetadataTest {

    private EventJsonMapper eventJsonMapper;

    @Before
    public void setUp() throws Exception {
        eventJsonMapper = new EventJsonMapper();
    }

    @Test
    public void deserializesMinimum() {
        val eventId = UUID.randomUUID();
        val jsonString = "{\"eventId\":\"" + eventId + "\"}";

        val result = eventJsonMapper.readValue(jsonString.getBytes(), SerializableMetadata.class).get();

        assertThat(result.eventId()).isEqualTo(eventId);
        assertThat(result.causality()).isNull();
    }

    @Test
    public void deserializesLegacyCausality() {
        final UUID causalityEventId = UUID.randomUUID();
        val jsonString = "{\"eventId\":\"" + UUID.randomUUID() + "\", " + "\"causality\": {\"eventId\": \""
                + causalityEventId + "\", \"eventType\": \"referenced\"}}";

        val result = eventJsonMapper.readValue(jsonString.getBytes(), SerializableMetadata.class).get();

        assertThat(result.causality()).containsExactly(CausalityData.of(Causality
            .builder()
            .eventId(EventId.of(causalityEventId))
            .eventType(EventType.of("referenced"))
            .build()));
    }

    @Test
    public void deserializesCausality() {
        final UUID causalityEventId = UUID.randomUUID();
        val jsonString = "{\"eventId\":\"" + UUID.randomUUID() + "\", " + "\"causality\": [{\"eventId\": \""
                + causalityEventId + "\", \"eventType\": \"referenced\"}]}";

        val result = eventJsonMapper.readValue(jsonString.getBytes(), SerializableMetadata.class).get();

        assertThat(result.causality()).containsExactly(CausalityData.of(Causality
            .builder()
            .eventId(EventId.of(causalityEventId))
            .eventType(EventType.of("referenced"))
            .build()));
    }

    @Test
    public void deserializesEventInitiator() {
        final UUID initiatorId = UUID.randomUUID();
        final String initiatorType = "INITIATOR";
        final UUID impersonatorId = UUID.randomUUID();
        final String impersonatorType = "IMPERSONATOR";
        val jsonString = "{\"eventId\":\"" + UUID.randomUUID() + "\", " + "\"eventInitiator\": {" + "\"id\":\""
                + initiatorId + "\", \"type\":\"" + initiatorType + "\" ," + "\"agent\": {\"id\":\"" + impersonatorId
                + "\", \"type\":\"" + impersonatorType + "\"}}}";

        val result = eventJsonMapper.readValue(jsonString.getBytes(), SerializableMetadata.class).get();

        System.out.println(result);

        assertThat(result.eventInitiator()).isEqualTo(EventInitiatorData.of(EventInitiator
            .builder()
            .id(initiatorId)
            .type(initiatorType)
            .agent(ReferenceData.of(EventInitiator.builder().id(impersonatorId).type(impersonatorType).build()))
            .build()));
    }

    @Test
    public void deserializesLegacyEventInitiator() {
        final UUID initiatorId = UUID.randomUUID();
        final String initiatorType = "INITIATOR";

        val jsonString = "{ \"eventId\": \"e8b89e2f-54cb-4e1a-ab44-2927715fd1e1\", " +
                "\"schemaRef\": \"https://event.store/test_event.json\", " +
                "\"version\": 0, \"eventType\": \"test\", \"eventInitiator\": " +
                "{ \"initiator\": { \"id\": \""+  initiatorId + "\", \"type\": \"" + initiatorType + "\" } } }";

        val result = eventJsonMapper.readValue(jsonString.getBytes(), SerializableMetadata.class).get();

        assertThat(result.eventInitiator()).isNotNull();
        assertThat(result.eventInitiator().id()).isNull();
        assertThat(result.eventInitiator().type()).isNull();
        assertThat(result.eventInitiator().initiator()).isPresent();
        final ReferenceData referenceData = result.eventInitiator().initiator().get();
        assertThat(referenceData.id()).isEqualTo(initiatorId);
        assertThat(referenceData.type()).isEqualTo(initiatorType);


    }
}