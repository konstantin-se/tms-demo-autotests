package com.tms.db

import io.testignite.database.connectors.TmsDB

// The actual connector lives in io.testignite.database.connectors.TmsDB (dtoGen source set) because
// DtoClassGenerator resolves it there by reflection; this alias keeps the project's DB layer in
// com.tms.db without a second container bootstrap.
typealias TmsDBConnector = TmsDB
