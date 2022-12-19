import React from 'react';

import PropTypes from 'prop-types';

import TableRow from 'components/form-elements/TableRow';
import Spinner from 'components/spinner/Spinner';


const TableBody = (props) => {
  const {
    fieldsConfig,
    properties,
    fields,
    tableRef = () => {
    },
    addRow = (row = {}) => fields.push(row),
  } = props;
  const RowComponent = properties.subfield ? TableRow : fieldsConfig.rowComponent || TableRow;

  const {
    emptySubstitutions,
    loadingSubstitutions,
  } = properties;

  if (loadingSubstitutions) {
    return (<Spinner />);
  }

  if (emptySubstitutions && !fields.length) {
    return (<div>There is no data to load</div>);
  }


  return (
    fields.map((field, index) => (
      <RowComponent
        key={properties.subfield ? `${properties.parentIndex}-${index}` : index}
        field={field}
        index={index}
        properties={{
          ...properties,
          rowCount: fields.length || 0,
        }}
        addRow={addRow}
        fieldsConfig={fieldsConfig}
        removeRow={() => fields.remove(index)}
        rowValues={fields.value[index]}
        rowRef={(el, fieldName) => tableRef(el, fieldName, index)}
      />))
  );
};

/** @component */
export default TableBody;

TableBody.propTypes = {
  fieldsConfig: PropTypes.shape({
    getDynamicAttr: PropTypes.func,
  }).isRequired,
  fields: PropTypes.oneOfType([
    PropTypes.shape({}),
    PropTypes.arrayOf(PropTypes.shape({})),
  ]).isRequired,
  properties: PropTypes.shape({}).isRequired,
  addRow: PropTypes.func,
  tableRef: PropTypes.func,
};

TableBody.defaultProps = {
  addRow: undefined,
  tableRef: undefined,
};
