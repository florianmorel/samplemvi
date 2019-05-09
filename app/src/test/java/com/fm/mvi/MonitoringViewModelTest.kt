package com.fm.mvi

import com.fm.mvi.model.MonitoringIntents
import com.fm.mvi.model.MonitoringViewState
import com.fm.mvi.presentation.MonitoringActionProcessor
import com.fm.mvi.presentation.MonitoringViewModel
import com.fm.mvi.service.AlertService
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations


class MonitoringViewModelTest {

    private lateinit var testObserver: TestObserver<MonitoringViewState>
    private lateinit var monitoringViewModel: MonitoringViewModel

    private var alertReceivedIntent = MonitoringIntents.ReceivedAlertMonitoringIntent("Sensor Unplugged")
    private var errorReceivedIntent = MonitoringIntents.ReceivedErrorMonitoringIntent

    @Before
    fun setupMocksAndView() {
        MockitoAnnotations.initMocks(this)
        monitoringViewModel = MonitoringViewModel(
            MonitoringActionProcessor(),
            AlertService()
        )
        testObserver = monitoringViewModel.states().test()
    }

    /**
     * TEST STOPPED TEST
     */

    @Test
    fun startingState() {
        testObserver.assertValueCount(1)
        testObserver.assertValue(
            MonitoringViewState.idle()
        )
    }

    @Test
    fun testStoppedState_StartEvent() {
        monitoringViewModel.processIntents(
            Observable.just(
                MonitoringIntents.StartMonitoringIntent
            )
        )
        testObserver.assertValueCount(2)
        assert(testObserver.values().last() is MonitoringViewState.Initialization)
    }

    @Test
    fun testStoppedState_IsNotListeningAlertEvent() {
        monitoringViewModel.processIntents(
            Observable.just(
                alertReceivedIntent
            )
        )
        testObserver.assertValueCount(1)
        assert(testObserver.values().last() is MonitoringViewState.Stopped)
    }

    @Test
    fun testStoppedState_ErrorEventReceived() {
        monitoringViewModel.processIntents(
            Observable.just(
                errorReceivedIntent
            )
        )

        testObserver.assertValueCount(2) //first one is stop state
        assert(testObserver.values().last() is MonitoringViewState.Error)
    }

    /**
     * TEST INIT STATE
     */
    @Test
    fun testInitState_IsNotListeningAlertEvent() {

        monitoringViewModel.processIntents(
            Observable.merge(
                Observable.just(MonitoringIntents.StartMonitoringIntent),
                Observable.just(alertReceivedIntent)
            )
        )
        testObserver.assertValueCount(2)
        assert(testObserver.values().last() is MonitoringViewState.Initialization)
    }

    @Test
    fun testInitState_ErrorEventReceived() {

        monitoringViewModel.processIntents(
            Observable.merge(
                Observable.just(MonitoringIntents.StartMonitoringIntent),
                Observable.just(errorReceivedIntent)
            )
        )
        testObserver.assertValueCount(3)
        assert(testObserver.values().last() is MonitoringViewState.Error)
    }

    @Test
    fun testInitState_InitializedEventReceived() {

        monitoringViewModel.processIntents(
            Observable.merge(
                Observable.just(MonitoringIntents.StartMonitoringIntent),
                Observable.just(MonitoringIntents.InitializedMonitoringIntent)
            )
        )
        testObserver.assertValueCount(3)
        assert(testObserver.values().last() is MonitoringViewState.Started)
    }

    /**
     * Test Started Test
     */
    @Test
    fun testStartedState_AlertEventReceived() {

        monitoringViewModel.processIntents(
            Observable.merge(
                Observable.just(MonitoringIntents.StartMonitoringIntent),
                Observable.just(MonitoringIntents.InitializedMonitoringIntent),
                Observable.just(alertReceivedIntent)
            )
        )
        testObserver.assertValueCount(4)
        assert(testObserver.values().last() is MonitoringViewState.Alert)
    }

    @Test
    fun testStartedState_ErrorEventReceived() {

        monitoringViewModel.processIntents(
            Observable.merge(
                Observable.just(MonitoringIntents.StartMonitoringIntent),
                Observable.just(MonitoringIntents.InitializedMonitoringIntent),
                Observable.just(errorReceivedIntent)
            )
        )
        testObserver.assertValueCount(4)
        assert(testObserver.values().last() is MonitoringViewState.Error)
    }

    @Test
    fun testStartedState_StopEvent() {

        monitoringViewModel.processIntents(
            Observable.merge(
                Observable.just(MonitoringIntents.StartMonitoringIntent),
                Observable.just(MonitoringIntents.InitializedMonitoringIntent),
                Observable.just(MonitoringIntents.StopMonitoringIntent)
            )
        )
        testObserver.assertValueCount(4)
        assert(testObserver.values().last() is MonitoringViewState.Stopped)
    }

    /**
     * TEST ERROR ALERT
     */
    @Test
    fun testErrorState_ResetEvent_ShouldGoToStop() {

        monitoringViewModel.processIntents(
            Observable.merge(
                Observable.just(errorReceivedIntent),
                Observable.just(MonitoringIntents.ResetMonitoringIntent)
            )
        )
        assert(testObserver.values().last() is MonitoringViewState.Stopped)
    }

    /**
     * TEST ALERT STATE
     */
    @Test
    fun testAlertState_ErrorReceived() {

        monitoringViewModel.processIntents(
            Observable.merge(
                Observable.just(MonitoringIntents.StartMonitoringIntent),
                Observable.just(MonitoringIntents.InitializedMonitoringIntent),
                Observable.just(alertReceivedIntent),
                Observable.just(errorReceivedIntent)
            )
        )
        testObserver.assertValueCount(5)
        testObserver.values().last().let {
            //check that we prepare Error State
            assert(it is MonitoringViewState.Alert && it.error)
        }
    }

    @Test
    fun testAlertState_CloseEventNoPendingError() {

        monitoringViewModel.processIntents(
            Observable.merge(
                Observable.just(MonitoringIntents.StartMonitoringIntent),
                Observable.just(MonitoringIntents.InitializedMonitoringIntent),
                Observable.just(alertReceivedIntent),
                Observable.just(MonitoringIntents.CloseAlertMonitoringIntent(false))
            )
        )
        testObserver.assertValueCount(5)
        testObserver.assertValueAt(3) { state -> state is MonitoringViewState.Alert && !state.error }
        assert(testObserver.values().last() is MonitoringViewState.Started)
    }

    @Test
    fun testAlertState_CloseEventPendingError() {

        monitoringViewModel.processIntents(
            Observable.merge(
                Observable.just(MonitoringIntents.StartMonitoringIntent),
                Observable.just(MonitoringIntents.InitializedMonitoringIntent),
                Observable.just(alertReceivedIntent),
                Observable.just(MonitoringIntents.CloseAlertMonitoringIntent(true))
            )
        )
        testObserver.assertValueCount(5)
        assert(testObserver.values().last() is MonitoringViewState.Error)
    }

}