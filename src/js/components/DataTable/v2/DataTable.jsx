import React from 'react';

import PropTypes from 'prop-types';

import DataTableBody from 'components/DataTable/v2/DataTableBody';
import DataTableFooter from 'components/DataTable/v2/DataTableFooter';
import DataTableHeader from 'components/DataTable/v2/DataTableHeader';
import useDataTable from 'hooks/useDataTable';

import 'components/DataTable/DataTable.scss';

const DataTable = ({
  columns,
  data,
  pageSize,
  footerComponent,
  loading,
  loadingMessage,
  emptyTableMessage,
}) => {
  const {
    defaultEmptyTableMessage,
    defaultLoadingTableMessage,
    canPrevious,
    canNext,
    pageSizeSelectOptions,
    onPageSizeChange,
    onPageChange,
    table,
    pages,
    pagination,
  } = useDataTable({
    pageSize,
    columns,
    data,
  });

  return (
    <div className="app-react-table-wrapper table-v2">
      <div className="ReactTable app-react-table">
        <div className="rt-table" role="grid">
          <DataTableHeader
            headerGroups={table.getHeaderGroups()}
          />
          <DataTableBody
            emptyTableMessage={emptyTableMessage}
            loadingMessage={loadingMessage}
            defaultLoadingTableMessage={defaultLoadingTableMessage}
            defaultEmptyTableMessage={defaultEmptyTableMessage}
            loading={loading}
            rowModel={table.getRowModel()}
            dataLength={data?.length}
          />
          <DataTableFooter
            footerComponent={footerComponent}
            pagination={pagination}
            onPageChange={onPageChange}
            onPageSizeChange={onPageSizeChange}
            data={data}
            canPrevious={canPrevious}
            canNext={canNext}
            pages={pages}
            pageSizeSelectOptions={pageSizeSelectOptions}
          />
        </div>
      </div>
    </div>
  );
};

export default DataTable;

DataTable.propTypes = {
  columns: PropTypes.arrayOf(
    PropTypes.shape({}),
  ).isRequired,
  data: PropTypes.arrayOf(
    PropTypes.shape({}),
  ).isRequired,
  pageSize: PropTypes.number,
  footerComponent: PropTypes.func,
  loading: PropTypes.bool,
  loadingMessage: PropTypes.shape({
    id: PropTypes.string.isRequired,
    defaultMessage: PropTypes.string.isRequired,
  }),
  emptyTableMessage: PropTypes.shape({
    id: PropTypes.string.isRequired,
    defaultMessage: PropTypes.string.isRequired,
  }),
};

DataTable.defaultProps = {
  footerComponent: null,
  loading: false,
  loadingMessage: null,
  emptyTableMessage: null,
  pageSize: 5,
};
