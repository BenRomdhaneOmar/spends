package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.infrastructure.repository

import com.benromdhane.omar.offroadsoft.monad.error.SuccessOrSingleError
import com.benromdhane.omar.offroadsoft.monad.error.asSuccessOrSingleError
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.model.PersistedSpend
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.model.Spend
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.repository.SpendsRepository
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.core.service.SpendsService
import org.jetbrains.annotations.TestOnly
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
object InMemorySpendsRepository : SpendsRepository {

    private val spends = CopyOnWriteArrayList<PersistedSpend>()

    override fun allSpends(): SuccessOrSingleError<Collection<PersistedSpend>, SpendsService.Error> =
        this.spends
            .toList()
            .let {
                when {
                    it.isEmpty() -> SuccessOrSingleError.Error.of(SpendsService.Error.Business.NoSpendsFound)
                    else -> SuccessOrSingleError.Success.of(it)
                }
            }

    override fun addSpend(spend: Spend): SuccessOrSingleError<PersistedSpend, SpendsService.Error> =
        PersistedSpend.of(
            Uuid.generateV7().toString(),
            spend
        )
            .mapSuccess {
                this.spends.add(it)
                it
            }
            .asSuccessOrSingleError()
            .mapError {
                SpendsService.Error.Infrastructure.error(
                    Exception(
                        it.joinToString(", ")
                    )
                )
            }

    @TestOnly
    fun deleteAllSpends() {
        this.spends
            .removeIf { true }
    }

    @TestOnly
    fun count() =
        this.spends.count()
}