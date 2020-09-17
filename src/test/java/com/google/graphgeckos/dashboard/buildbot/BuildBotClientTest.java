package com.google.graphgeckos.dashboard.buildbot;

import com.google.graphgeckos.dashboard.datatypes.BuildBotData;
import com.google.graphgeckos.dashboard.fetchers.buildbot.BuildBotClient;
import com.google.graphgeckos.dashboard.storage.DatastoreRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BuildBotClientTest {

  static class BuildBotDataMatcher implements ArgumentMatcher<BuildBotData> {

    /**
     * Checks if an instance of the {@link BuildBotData} provided by the tested {@code client}
     * as an argument when calling {@link DatastoreRepository::updateRevisionEntry}.
     *
     * @param data instance of the {@link BuildBotData} to inspect.
     * @return true if the {@code data} is valid {@see BuildBotData::isValid}.
     */
    @Override
    public boolean matches(BuildBotData data) {
      return data.getCommitHash() != null
        && data.getName() != null
        && data.getStatus() != null
        && data.getLogs() != null;
    }
  }

  /**
   * Tested BuildBot API fetcher.
   */
  @InjectMocks
  BuildBotClient client = new BuildBotClient(
    "http://lab.llvm.org:8011/json/builders");

  @Mock
  DatastoreRepository datastoreRepository;

  /**
   * Enables Mockito.
   */
  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Build Bot name.
   */
  public final String VALID_BUILD_BOT = "clang-x86_64-debian-fast";

  /**
   * Initial build ids.
   */
  public final int VALID_BUILD_ID = 36624;
  public final int INVALID_BUILD_ID = 0;

  /**
   * Request frequency.
   */
  public final long DELAY_ONE_SECOND = 1;

  private long secondsToMillis(long seconds) {
    return seconds * 1000;
  }

  /**
   * Should add fetched data in the form of {@link BuildBotData} POJO
   * to the storage via an instance of {@link DatastoreRepository}.
   * <p>
   * Given: valid build bot name, build id and delay.
   * Expected behaviour: fetches JSON, turns JSON into a valid (no null fields) POJO,
   * passes this object as an argument to {@link DatastoreRepository::updateEntry}.
   */
  @Test
  public void validResponseCausesUpdateCallToRepositoryWithValidArguments() {
    client.run(VALID_BUILD_BOT, VALID_BUILD_ID, DELAY_ONE_SECOND);

    // Wait to check if the datastoreRepository::updateRevisionEntry was called.
    long wait = secondsToMillis(DELAY_ONE_SECOND) * 3;
    Mockito.verify(datastoreRepository, Mockito.after(wait))
      .updateRevisionEntry(Mockito.argThat(new BuildBotDataMatcher()));
  }

  /**
   * Shouldn't attempt to access the storage via an instance of {@link DatastoreRepository}.
   * <p>
   * Given: invalid arguments (build id).
   * Expected behaviour: doesn't access storage or throw any Exception.
   */
  @Test
  public void invalidResponseCausesNoCallToRepository() {
    client.run(VALID_BUILD_BOT, INVALID_BUILD_ID, DELAY_ONE_SECOND);

    Mockito.verify(datastoreRepository, Mockito.never()).updateRevisionEntry(Mockito.any());
  }

}
