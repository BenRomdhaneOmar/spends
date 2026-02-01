package com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.repository

import com.benromdhane.omar.offroadsoft.monad.error.SuccessOrSingleError
import com.benromdhane.omar.offroadsoft.monad.error.asSuccessOrSingleError
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.model.PersistedSpend
import com.benromdhane.omar.offroadsoft.turtlesandrabbits.spends.model.Spend
import org.jetbrains.annotations.TestOnly
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class InMemorySpendsRepository private constructor(
    private val spends: CopyOnWriteArrayList<PersistedSpend>
) : SpendsRepository {

    companion object {

        private val spends = CopyOnWriteArrayList<PersistedSpend>()
        private val instanceLock = ReentrantLock()
        private var instance: InMemorySpendsRepository? = null

        fun instance() =
            instanceLock
                .withLock {
                    instance
                        ?: InMemorySpendsRepository(
                            spends
                        )
                            .also { instance = it }
                }
    }

    override fun allSpends(): SuccessOrSingleError<Collection<PersistedSpend>, SpendsRepository.Error> =
        this.spends
            .toList()
            .let {
                when {
                    it.isEmpty() -> SuccessOrSingleError.Error.of(SpendsRepository.Error.Business.NoSpendsFound)
                    else -> SuccessOrSingleError.Success.of(it)
                }
            }

    override fun addSpend(spend: Spend): SuccessOrSingleError<PersistedSpend, SpendsRepository.Error> =
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
                SpendsRepository.Error.Infrastructure.error(
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