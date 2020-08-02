package org.pih.warehouse.apitablero

import grails.converters.JSON
import grails.plugin.cache.Cacheable
import org.pih.warehouse.core.Location
import grails.gorm.transactions.Transactional
import org.pih.warehouse.core.User
import org.pih.warehouse.tablero.NumberData

@Transactional
class ApitableroController {

    def numberDataService
    def indicatorDataService
    def userService
    def messageService
    def grailsApplication

    def config() {
        User user = User.get(session.user.id)
        def config = userService.getDashboardConfig(user)

        render(config as JSON)
    }

    def updateConfig() {
        User user = User.get(session.user.id)
        def config = userService.updateDashboardConfig(user, request.JSON)

        render(config as JSON)
    }

    def breadcrumbsConfig() {
        render(grailsApplication.config.breadcrumbsConfig as JSON)
    }

    def getInventoryByLotAndBin() {
        Location location = Location.get(params.locationId)
        NumberData numberData = numberDataService.getInventoryByLotAndBin(location)
        render(numberData as JSON)
    }

    def getInProgressShipments() {
        Location location = Location.get(params.locationId)
        NumberData numberData = numberDataService.getInProgressShipments(session.user, location)
        render(numberData as JSON)
    }

    def getInProgressPutaways() {
        Location location = Location.get(params.locationId)
        NumberData numberData = numberDataService.getInProgressPutaways(session.user, location)
        render(numberData as JSON)
    }

    def getReceivingBin() {
        Location location = Location.get(params.locationId)
        NumberData numberData = numberDataService.getReceivingBin(location)
        render(numberData as JSON)
    }

    def getItemsInventoried() {
        Location location = Location.get(params.locationId)
        NumberData numberData = numberDataService.getItemsInventoried(location)
        render(numberData as JSON)
    }

    def getDefaultBin() {
        Location location = Location.get(params.locationId)
        NumberData numberData = numberDataService.getDefaultBin(location)
        render(numberData as JSON)
    }

    def getExpiredProductsInStock() {
        Location location = Location.get(params.locationId)
        NumberData numberData = numberDataService.getExpiredProductsInStock(location)
        render (numberData as JSON)
    }

    def getExpirationSummary() {
        Location location = Location.get(params.locationId)
        def expirationSummary = indicatorDataService.getExpirationSummaryData(location, params)
        render(expirationSummary.toJson() as JSON)
    }

    def getFillRate() {
        Location location = Location.get(params.locationId)
        Location destination = Location.get(params.destinationLocation)
        def fillRate = indicatorDataService.getFillRate(location, destination, params)
        render(fillRate.toJson() as JSON)
    }

    def getFillRateSnapshot() {
        Location location = Location.get(params.locationId)
        def fillRateSnapshot = indicatorDataService.getFillRateSnapshot(location, params)
        render(fillRateSnapshot.toJson() as JSON)
    }

    def getFillRateDestinations() {
        Location location = Location.get(params.locationId?:session.warehouse.id)
        def destinations = []
        def defaultDestination = [code : "react.dashboard.locationFilter.all.label", message : messageService.getMessage("react.dashboard.locationFilter.all")]
        destinations << [id: "", name: defaultDestination]
        destinations.addAll(indicatorDataService.getFillRateDestinations(location))
        render([data: destinations] as JSON)
    }

    def getInventorySummary() {
        Location location = Location.get(params.locationId)
        def inventorySummary = indicatorDataService.getInventorySummaryData(location)
        render(inventorySummary.toJson() as JSON)
    }

    def getSentStockMovements() {
        Location location = Location.get(params.locationId)
        def sentStockMovements = indicatorDataService.getSentStockMovements(location, params)
        render(sentStockMovements.toJson() as JSON)
    }

    def getReceivedStockMovements() {
        Location location = Location.get(params.locationId)
        def receivedStockMovements = indicatorDataService.getReceivedStockData(location, params)
        render(receivedStockMovements.toJson() as JSON)
    }

    def getOutgoingStock() {
        Location location = Location.get(params.locationId)
        def outgoingStock = indicatorDataService.getOutgoingStock(location)
        render(outgoingStock.toJson() as JSON)
    }

    def getIncomingStock() {
        Location location = Location.get(params.locationId)
        def incomingStock = indicatorDataService.getIncomingStock(location)
        render(incomingStock.toJson() as JSON)
    }

    def getDiscrepancy() {
        Location location = Location.get(params.locationId)
        def discrepancy = indicatorDataService.getDiscrepancy(location, params)
        render(discrepancy as JSON)
    }

    def getDelayedShipments() {
        Location location = Location.get(params.locationId)
        def delayedShipments = indicatorDataService.getDelayedShipments(location, request.contextPath)
        render(delayedShipments as JSON)
    }

    def getProductWithNegativeInventory() {
        Location location = Location.get(params.locationId)
        def productsWithNegativeInventory = numberDataService.getProductWithNegativeInventory(location)
        render(productsWithNegativeInventory as JSON)
    }

    def getLossCausedByExpiry() {
        Location location = Location.get(params.locationId)
        def lossCausedByExpiry = indicatorDataService.getLossCausedByExpiry(location, params)
        render (lossCausedByExpiry.toJson() as JSON)
    }

    def getProductsInventoried() {
        Location location = Location.get(params.locationId)
        def productsInventoried = indicatorDataService.getProductsInventoried(location)
        render (productsInventoried.toJson() as JSON)
     }

    def getPercentageAdHoc() {
        Location location = Location.get(session?.warehouse?.id)
        def percentageAdHoc = indicatorDataService.getPercentageAdHoc(location)
        render (percentageAdHoc.toJson() as JSON)
     }

    def getStockOutLastMonth() {
        Location location = Location.get(session?.warehouse?.id)
        def stockOutLastMonth = indicatorDataService.getStockOutLastMonth(location)
        render (stockOutLastMonth.toJson() as JSON)
     }

    def getOpenStockRequests() {
        Location location = Location.get(params.locationId)
        NumberData numberData = numberDataService.getOpenStockRequests(location)
        render (numberData as JSON)
    }

    def getInventoryValue() {
        Location location = Location.get(params.locationId)
        NumberData numberData = numberDataService.getInventoryValue(location)
        render (numberData as JSON)
    }
}
