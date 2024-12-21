package es.us.managemyteam.repository.util

import androidx.lifecycle.Observer
import es.managemyteam.data.R

abstract class ResourceObserver<T> : Observer<Resource<T>> {

    override fun onChanged(value: Resource<T>) {
        when {
            value.isSuccess() -> {
                onLoading(false)
                onSuccess(value.data)
            }

            value.isError() -> {
                onLoading(false)
                value.error?.let { error ->
                    onError(error)
                }
            }

            else -> {
                onLoading(true)
            }
        }
    }

    abstract fun onSuccess(response: T?)

    open fun onError(error: Error) {
        if (error.isUnknown()) {
            error.errorMessageId = R.string.unknown_error
        }
    }

    open fun onLoading(loading: Boolean) {
        // no-op
    }

}