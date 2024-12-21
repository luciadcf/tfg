package es.us.managemyteam.di

import es.us.managemyteam.contract.PaypalInterface
import es.us.managemyteam.manager.PaypalManager
import es.us.managemyteam.ui.viewmodel.AcceptPlayersViewModel
import es.us.managemyteam.ui.viewmodel.AcceptedCallViewModel
import es.us.managemyteam.ui.viewmodel.AdminPaypalViewModel
import es.us.managemyteam.ui.viewmodel.ChatViewModel
import es.us.managemyteam.ui.viewmodel.ClubViewModel
import es.us.managemyteam.ui.viewmodel.CreateCoachViewModel
import es.us.managemyteam.ui.viewmodel.CreateEventViewModel
import es.us.managemyteam.ui.viewmodel.CreatePaymentViewModel
import es.us.managemyteam.ui.viewmodel.EditClubViewModel
import es.us.managemyteam.ui.viewmodel.EventDetailViewModel
import es.us.managemyteam.ui.viewmodel.EventsViewModel
import es.us.managemyteam.ui.viewmodel.LoginViewModel
import es.us.managemyteam.ui.viewmodel.MenuViewModel
import es.us.managemyteam.ui.viewmodel.MyPaymentsViewModel
import es.us.managemyteam.ui.viewmodel.MyTeamViewModel
import es.us.managemyteam.ui.viewmodel.PastEventsViewModel
import es.us.managemyteam.ui.viewmodel.PendingCallViewModel
import es.us.managemyteam.ui.viewmodel.RecoverPasswordViewModel
import es.us.managemyteam.ui.viewmodel.RegistrationViewModel
import es.us.managemyteam.ui.viewmodel.RejectedCallViewModel
import es.us.managemyteam.ui.viewmodel.UnableUsersViewModel
import es.us.managemyteam.ui.viewmodel.UpdateEmailViewModel
import es.us.managemyteam.ui.viewmodel.UpdatePasswordViewModel
import es.us.managemyteam.ui.viewmodel.UpdateUserProfileViewModel
import es.us.managemyteam.ui.viewmodel.UserProfileViewModel
import es.us.managemyteam.usecase.AcceptCallUc
import es.us.managemyteam.usecase.AcceptPlayerUc
import es.us.managemyteam.usecase.CreateEventUc
import es.us.managemyteam.usecase.CreatePaymentUc
import es.us.managemyteam.usecase.CreatePaypalConfigUc
import es.us.managemyteam.usecase.EditClubUc
import es.us.managemyteam.usecase.GetCallsByUserIdUC
import es.us.managemyteam.usecase.GetClubUc
import es.us.managemyteam.usecase.GetCurrentCallUc
import es.us.managemyteam.usecase.GetCurrentNewEventUc
import es.us.managemyteam.usecase.GetCurrentNewUserUc
import es.us.managemyteam.usecase.GetEventDetailUc
import es.us.managemyteam.usecase.GetEventLocationUc
import es.us.managemyteam.usecase.GetEventsUc
import es.us.managemyteam.usecase.GetMessagesUc
import es.us.managemyteam.usecase.GetMyPaymentsUc
import es.us.managemyteam.usecase.GetPaypalConfigUc
import es.us.managemyteam.usecase.GetPlayersUc
import es.us.managemyteam.usecase.GetUserDeviceIdsUc
import es.us.managemyteam.usecase.GetUserUc
import es.us.managemyteam.usecase.GetUsersUc
import es.us.managemyteam.usecase.LoginUc
import es.us.managemyteam.usecase.LogoutUc
import es.us.managemyteam.usecase.PostMessageUc
import es.us.managemyteam.usecase.PostRegistrationUc
import es.us.managemyteam.usecase.RecoverPasswordUc
import es.us.managemyteam.usecase.RejectCallUc
import es.us.managemyteam.usecase.RemoveUserUc
import es.us.managemyteam.usecase.SendNotificationsUc
import es.us.managemyteam.usecase.SetCurrentCallUc
import es.us.managemyteam.usecase.SetCurrentDeviceIdUc
import es.us.managemyteam.usecase.SetCurrentNewEventUc
import es.us.managemyteam.usecase.SetCurrentNewUserUc
import es.us.managemyteam.usecase.SetEventLocationUc
import es.us.managemyteam.usecase.UpdateEmailUc
import es.us.managemyteam.usecase.UpdatePasswordUc
import es.us.managemyteam.usecase.UpdateUserProfileUc
import es.us.managemyteam.usecase.UpdateUsersEnabledUc
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // General

    single<PaypalInterface> {
        PaypalManager().apply { initialize(get()) }
    }

    viewModel {
        MenuViewModel(get(), get())
    }

    // Eventos

    factory {
        GetEventLocationUc(get())
    }

    factory {
        SetEventLocationUc(get())
    }

    factory {
        GetEventsUc(get())
    }

    factory {
        CreateEventUc(get())
    }

    factory {
        GetCurrentNewEventUc(get())
    }

    factory {
        SetCurrentNewEventUc(get())
    }

    factory {
        GetEventDetailUc(get())
    }

    factory {
        GetCurrentCallUc(get())
    }

    factory {
        SetCurrentCallUc(get())
    }

    factory {
        SetCurrentDeviceIdUc(get())
    }

    viewModel {
        EventsViewModel(get(), get(), get(), get())
    }

    viewModel {
        CreateEventViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get())
    }

    viewModel {
        ChatViewModel(get(), get(), get())
    }

    viewModel {
        MyTeamViewModel(get(), get())
    }

    viewModel {
        EventDetailViewModel(get(), get())
    }

    viewModel {
        PastEventsViewModel(get(), get())
    }

    // Club

    factory {
        GetClubUc(get())
    }

    factory {
        EditClubUc(get())
    }

    viewModel {
        ClubViewModel(get(), get())
    }

    viewModel {
        EditClubViewModel(get(), get(), get())
    }

    //Payment

    factory {
        CreatePaymentUc(get())
    }

    factory {
        GetMyPaymentsUc(get())
    }

    factory {
        GetPaypalConfigUc(get())
    }

    factory {
        CreatePaypalConfigUc(get())
    }

    viewModel {
        AdminPaypalViewModel(get(), get())
    }

    viewModel {
        MyPaymentsViewModel(get())
    }

    viewModel {
        CreatePaymentViewModel(get(), get())
    }

    // User

    factory {
        PostRegistrationUc(get())
    }

    factory {
        LoginUc(get())
    }

    factory {
        GetUserUc(get())
    }

    factory {
        LogoutUc(get())
    }

    factory {
        RemoveUserUc(get())
    }

    factory {
        UpdateUserProfileUc(get())
    }

    factory {
        UpdateEmailUc(get())
    }

    factory {
        UpdatePasswordUc(get())
    }

    factory {
        RecoverPasswordUc(get())
    }

    factory {
        SetCurrentNewUserUc(get())
    }

    factory {
        GetCurrentNewUserUc(get())
    }

    factory {
        GetUserDeviceIdsUc(get())
    }

    viewModel {
        RegistrationViewModel(get(), get(), get())
    }

    viewModel {
        LoginViewModel(get(), get())
    }

    viewModel {
        UserProfileViewModel(get(), get())
    }

    viewModel {
        UpdateUserProfileViewModel(get(), get())
    }

    viewModel {
        UpdateEmailViewModel(get(), get())
    }

    viewModel {
        UpdatePasswordViewModel(get(), get(), get())
    }

    viewModel {
        RecoverPasswordViewModel(get())
    }

    // Chat

    factory {
        GetMessagesUc(get())
    }

    factory {
        PostMessageUc(get())
    }

    // Administration

    factory {
        GetPlayersUc(get())
    }

    factory {
        AcceptPlayerUc(get())
    }

    factory {
        GetUsersUc(get())
    }

    factory {
        UpdateUsersEnabledUc(get())
    }

    viewModel {
        AcceptPlayersViewModel(get(), get(), get())
    }

    viewModel {
        CreateCoachViewModel(get())
    }

    viewModel {
        UnableUsersViewModel(get(), get())
    }

    // Call

    factory {
        GetCallsByUserIdUC(get())
    }

    factory {
        AcceptCallUc(get())
    }

    factory {
        RejectCallUc(get())
    }

    viewModel {
        PendingCallViewModel(get(), get(), get())
    }

    viewModel {
        AcceptedCallViewModel(get(), get())
    }

    viewModel {
        RejectedCallViewModel(get(), get())
    }

    // Notification

    factory {
        SendNotificationsUc(get())
    }

}