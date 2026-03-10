package com.rizero.feature_authorization.component.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.rizero.feature_authorization.SessionLoadStore
import com.rizero.feature_authorization.SessionLoadStoreFactory
import com.rizero.feature_authorization.component.InitialSessionLoadComponent
import com.rizero.feature_authorization.component.OfflineContinueDialogComponent
import com.rizero.shared_core_data.model.Session
import com.rizero.shared_core_data.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single

class DefaultInitialSessionLoadComponent(
    val componentContext: ComponentContext,
    val sessionRepository: SessionRepository,
    val storeFactory: StoreFactory = DefaultStoreFactory(),
    val authorizedCallback : (session : Session, refreshed : Boolean) -> Unit,
    val onOfflineContinue : (oldSession : Session) -> Unit,
    val navigateToAuthorization : (oldSession : Session?) -> Unit
) : InitialSessionLoadComponent, ComponentContext by componentContext {
    val store = SessionLoadStoreFactory(
        storeFactory = storeFactory,
        sessionRepository = sessionRepository
    ).create()
    val scope = coroutineScope()

    private val continueOfflineDialogSlot = SlotNavigation<ContinueOfflineDialogConfiguration>()

    override val continueOfflineDialog : Value<ChildSlot<*, OfflineContinueDialogComponent>> = childSlot(
        source = continueOfflineDialogSlot,
        serializer = ContinueOfflineDialogConfiguration.serializer(),
        handleBackButton = false,
    ){ cfg,context ->
        InitialLoadingOfflineContinueDialogComponent(
            componentContext = context,
            oldSession = cfg.oldCachedSession,
            cause = cfg.cause,
            continueOfflineCallback = { oldUserSession ->
                onOfflineContinue(oldUserSession)
            },
            tryAuthorizeCallback = { oldUserSession ->
                navigateToAuthorization(oldUserSession)
            }
        )
    }

    fun showContinueOfflineDialog(oldSession : Session,cause : OfflineContinueDialogComponent.ErrorCause){
        continueOfflineDialogSlot.activate(
            ContinueOfflineDialogConfiguration(oldCachedSession = oldSession,cause = cause)
        )
    }

    override val stateFlow: StateFlow<SessionLoadStore.State> = store.stateFlow(lifecycle)

    init {
        scope.launch {
            store.labels.collect { label ->
                when(label) {
                    SessionLoadStore.Label.SessionNotFound -> { navigateToAuthorization(null) }
                    is SessionLoadStore.Label.SessionInvalid -> {
                        when(label.cause) {
                            SessionLoadStore.Label.SessionInvalid.Cause.Expired,
                            SessionLoadStore.Label.SessionInvalid.Cause.NotValidOnServer ->
                                showContinueOfflineDialog(label.session, OfflineContinueDialogComponent.ErrorCause.TOKEN_NOT_VALID)
                            SessionLoadStore.Label.SessionInvalid.Cause.NetworkCheckUnavailable ->
                                showContinueOfflineDialog(label.session, OfflineContinueDialogComponent.ErrorCause.NETWORK_UNAVAILABLE)
                        }
                    }
                    is SessionLoadStore.Label.SessionValid -> {
                        authorizedCallback(label.session, label.refreshed)
                    }
                }
            }
        }
    }

    @Serializable
    data class ContinueOfflineDialogConfiguration(
        val oldCachedSession: Session,
        val cause : OfflineContinueDialogComponent.ErrorCause
    )

    @Single
    class ComponentFactory(val sessionRepository: SessionRepository) : InitialSessionLoadComponent.Factory{
        override fun invoke(
            componentContext : ComponentContext,
            authorizedCallback: (session: Session, refreshed: Boolean) -> Unit,
            onOfflineContinue: (oldSession: Session) -> Unit,
            navigateToAuthorization: (oldSession: Session?) -> Unit
        ): InitialSessionLoadComponent =
            DefaultInitialSessionLoadComponent(
                componentContext = componentContext,
                sessionRepository = sessionRepository,
                authorizedCallback = authorizedCallback,
                onOfflineContinue = onOfflineContinue,
                navigateToAuthorization = navigateToAuthorization
            )

    }
}