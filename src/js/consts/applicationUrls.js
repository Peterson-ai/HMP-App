/**
 * Definitions of APPLICATION URLs used for redirecting to pages
 * */

export const CONTEXT_PATH = window.CONTEXT_PATH ?? '/openboxes';

const DASHBOARD_URL = {
  base: '/dashboard',
};

const LOCATION_CONFIGURATION_URL = {
  base: '/locationsConfiguration',
  create: () => `${LOCATION_CONFIGURATION_URL.base}/create`,
  edit: (id) => `${LOCATION_CONFIGURATION_URL.create()}/${id}`,
  upload: () => `${LOCATION_CONFIGURATION_URL.base}/upload`,
};

const PRODUCT_CONFIGURATION_URL = {
  base: '/productsConfiguration',
  index: () => `${PRODUCT_CONFIGURATION_URL.base}/index`,
};

const PRODUCT_URL = {
  base: '/product',
  list: () => `${CONTEXT_PATH}${PRODUCT_URL.base}/list`,
  create: () => `${CONTEXT_PATH}${PRODUCT_URL.base}/create`,
  importCSV: () => `${CONTEXT_PATH}${PRODUCT_URL.base}/importAsCsv`,
};

const LOCATION_URL = {
  base: '/location',
  list: () => `${CONTEXT_PATH}${LOCATION_URL.base}/list`,
};

const STOCK_MOVEMENT_URL = {
  base: '/stockMovement',
  list: () => `${STOCK_MOVEMENT_URL.base}/list`,
  createInbound: () => `${STOCK_MOVEMENT_URL.base}/createInbound`,
  createOutbound: () => `${STOCK_MOVEMENT_URL.base}/createOutbound`,
  createCombinedShipments: () => `${STOCK_MOVEMENT_URL.base}/createCombinedShipments`,
  genericEdit: (id) => `${STOCK_MOVEMENT_URL.base}/edit/${id}`,
  edit: (id) => `${STOCK_MOVEMENT_URL.base}/edit/${id}`,
  editInbound: (id) => `${STOCK_MOVEMENT_URL.createInbound()}/${id}`,
  editOutbound: (id) => `${STOCK_MOVEMENT_URL.createOutbound()}/${id}`,
  editCombinedShipments: (id) => `${STOCK_MOVEMENT_URL.createCombinedShipments()}/${id}`,
  show: (id) => `${CONTEXT_PATH}${STOCK_MOVEMENT_URL.base}/show/${id}`,
};

const REQUEST_URL = {
  base: STOCK_MOVEMENT_URL.base,
  create: () => `${REQUEST_URL.base}/createRequest`,
  edit: (id) => `${REQUEST_URL.create()}/${id}`,
};

const INVOICE_URL = {
  base: '/invoice',
  create: () => `${INVOICE_URL.base}/create`,
  edit: (id) => `${INVOICE_URL.create()}/${id}`,
  show: (id) => `${CONTEXT_PATH}${INVOICE_URL.base}/show/${id}`,
  addDocument: (id) => `${CONTEXT_PATH}${INVOICE_URL.base}/addDocument/${id}`,
};

const PUTAWAY_URL = {
  base: '/putAway',
  create: () => `${PUTAWAY_URL.base}/create`,
  edit: (id) => `${PUTAWAY_URL.create()}/${id}`,
};

const STOCK_TRANSFER_URL = {
  base: '/stockTransfer',
  create: () => `${STOCK_TRANSFER_URL.base}/create`,
  createOutbound: () => `${STOCK_TRANSFER_URL.base}/createOutboundReturn`,
  createInbound: () => `${STOCK_TRANSFER_URL.base}/createInboundReturn`,
  genericEdit: (id) => `${STOCK_TRANSFER_URL.base}/edit/${id}`,
  edit: (id) => `${STOCK_TRANSFER_URL.create()}/${id}`,
  editOutbound: (id) => `${STOCK_TRANSFER_URL.createOutbound()}/${id}`,
  editInbound: (id) => `${STOCK_TRANSFER_URL.createInbound()}/${id}`,
  show: (id) => `${CONTEXT_PATH}${STOCK_TRANSFER_URL.base}/show/${id}`,
  print: (id) => `${CONTEXT_PATH}${STOCK_TRANSFER_URL.base}/print/${id}`,
};

const ORDER_URL = {
  base: '/order',
  list: () => `${CONTEXT_PATH}${ORDER_URL.base}/list`,
  create: () => `${CONTEXT_PATH}${ORDER_URL.base}/create`,
  show: (id) => `${CONTEXT_PATH}${ORDER_URL.base}/show/${id}`,
  print: (id) => `${CONTEXT_PATH}${ORDER_URL.base}/print/${id}`,
  addComment: (id) => `${CONTEXT_PATH}${ORDER_URL.base}/addComment/${id}`,
  addDocument: (id) => `${CONTEXT_PATH}${ORDER_URL.base}/addDocument/${id}`,
  placeOrder: (id) => `${CONTEXT_PATH}${ORDER_URL.base}/placeOrder/${id}`,
};

const PURCHASE_ORDER_URL = {
  base: '/purchaseOrder',
  create: () => `${PURCHASE_ORDER_URL.base}/create`,
  edit: (id) => `${PURCHASE_ORDER_URL.base}/edit/${id}`,
  addItems: (id) => `${PURCHASE_ORDER_URL.base}/addItems/${id}`,
};

const INVENTORY_ITEM_URL = {
  base: '/inventoryItem',
  showStockCard: (id) => `${CONTEXT_PATH}${INVENTORY_ITEM_URL.base}/showStockCard/${id}`,
};

const REQUISITION_TEMPLATE_URL = {
  base: '/requisitionTemplate',
  create: () => `${CONTEXT_PATH}${REQUISITION_TEMPLATE_URL.base}/create`,
  show: (id) => `${CONTEXT_PATH}${REQUISITION_TEMPLATE_URL.base}/show/${id}`,
  edit: (id) => `${CONTEXT_PATH}${REQUISITION_TEMPLATE_URL.base}/edit/${id}`,
  batch: (id) => `${CONTEXT_PATH}${REQUISITION_TEMPLATE_URL.base}/batch/${id}`,
  editHeader: (id) => `${CONTEXT_PATH}${REQUISITION_TEMPLATE_URL.base}/editHeader/${id}`,
};

const STOCKLIST_URL = {
  base: '/stocklist',
  pdf: (id) => `${CONTEXT_PATH}${STOCKLIST_URL.base}/renderPdf/${id}`,
  csv: (id) => `${CONTEXT_PATH}${STOCKLIST_URL.base}/generateCsv/${id}`,
};

const REPLENISHMENT_URL = {
  base: '/replenishment',
  create: () => `${REPLENISHMENT_URL.base}/create`,
  edit: (id) => `${REPLENISHMENT_URL.create()}/${id}`,
  print: (id) => `${CONTEXT_PATH}${REPLENISHMENT_URL.base}/print/${id}`,
};

const CATEGORY_URL = {
  base: '/category',
  tree: () => `${CONTEXT_PATH}${CATEGORY_URL.base}/tree`,
};

export {
  CATEGORY_URL,
  DASHBOARD_URL,
  INVENTORY_ITEM_URL,
  INVOICE_URL,
  LOCATION_CONFIGURATION_URL,
  LOCATION_URL,
  ORDER_URL,
  PRODUCT_CONFIGURATION_URL,
  PRODUCT_URL,
  PURCHASE_ORDER_URL,
  PUTAWAY_URL,
  REPLENISHMENT_URL,
  REQUEST_URL,
  REQUISITION_TEMPLATE_URL,
  STOCK_MOVEMENT_URL,
  STOCK_TRANSFER_URL,
  STOCKLIST_URL,
};
