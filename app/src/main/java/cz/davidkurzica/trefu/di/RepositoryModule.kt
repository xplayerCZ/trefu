package cz.davidkurzica.trefu.di

import cz.davidkurzica.trefu.data.repository.rest.ConnectionRepositoryImpl
import cz.davidkurzica.trefu.data.repository.rest.DepartureRepositoryImpl
import cz.davidkurzica.trefu.data.repository.rest.LineRepositoryImpl
import cz.davidkurzica.trefu.data.repository.rest.RouteRepositoryImpl
import cz.davidkurzica.trefu.data.repository.rest.StopRepositoryImpl
import cz.davidkurzica.trefu.data.repository.rest.TimetableRepositoryImpl
import cz.davidkurzica.trefu.domain.repository.*
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {

    singleOf(::ConnectionRepositoryImpl) { bind<ConnectionRepository>() }
    singleOf(::DepartureRepositoryImpl) { bind<DepartureRepository>() }
    singleOf(::LineRepositoryImpl) { bind<LineRepository>() }
    singleOf(::RouteRepositoryImpl) { bind<RouteRepository>() }
    singleOf(::StopRepositoryImpl) { bind<StopRepository>() }
    singleOf(::TimetableRepositoryImpl) { bind<TimetableRepository>() }
}