package contracts.user

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
    request {
        method DELETE()
        url("/api/v1/users/0199c60b-0dce-7fee-9ef2-d6dc30a8e3fa")
    }
    response {
        status HttpStatus.NO_CONTENT.value()
    }
}