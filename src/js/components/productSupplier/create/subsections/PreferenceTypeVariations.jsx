import React from 'react';

import _ from 'lodash';
import PropTypes from 'prop-types';
import { useFieldArray } from 'react-hook-form';
import { RiAddLine } from 'react-icons/ri';

import DataTable from 'components/DataTable';
import Button from 'components/form-elements/Button';
import Subsection from 'components/Layout/v2/Subsection';
import InvalidItemsIndicator from 'components/productSupplier/create/InvalidItemsIndicator';
import usePreferenceTypeVariationsColumns
  from 'hooks/productSupplier/form/usePreferenceTypeVariationsColumns';

const PreferenceTypeVariations = ({ control, errors }) => {
  const { fields, remove, append } = useFieldArray({
    control,
    name: 'productSupplierPreferences',
  });

  const { columns, translate } = usePreferenceTypeVariationsColumns({
    errors,
    control,
    remove,
  });

  const defaultTableRow = {
    destinationParty: {},
    preferenceType: {},
    validityStartDate: '',
    validityEndDate: '',
    bidName: '',
  };

  return (
    <Subsection
      collapsable
      title={{
        label: 'react.productSupplier.subsection.preferenceTypeVariations.title',
        defaultMessage: 'Preference Type Variations',
      }}
    >
      <div className="preference-type-variations-subsection">
        <div className="d-flex justify-content-end align-items-center mb-3">
          <InvalidItemsIndicator
            className="mr-3"
            setFilterInvalid={setFilterInvalid}
            errorsCounter={_.filter(errors)?.length}
          />
          <Button
            onClick={() => append(defaultTableRow)}
            StartIcon={<RiAddLine className="button-add-icon" />}
            defaultLabel="Add new"
            label="react.productSupplier.table.addNew.label"
          />
        </div>
        <DataTable
          data={fields}
          columns={columns}
          defaultPageSize={4}
          pageSize={fields.length <= 4 ? 4 : fields.length}
          showPagination={false}
          noDataText={translate(
            'react.productSupplier.table.empty.label',
            'No Preference Type Variations to display',
          )}
          loading={false}
          filterable
          filterAll
          defaultFilterMethod={(filter, row, column) => {
            console.log(row)
            return true;
          }}
        />
      </div>
    </Subsection>
  );
};

export default PreferenceTypeVariations;

PreferenceTypeVariations.propTypes = {
  control: PropTypes.shape({}).isRequired,
  errors: PropTypes.arrayOf(
    PropTypes.shape({
      destinationParty: PropTypes.shape({
        message: PropTypes.string,
      }),
      preferenceType: PropTypes.shape({
        message: PropTypes.string,
      }),
      validityStartDate: PropTypes.shape({
        message: PropTypes.string,
      }),
      validityEndDate: PropTypes.shape({
        message: PropTypes.string,
      }),
      bidName: PropTypes.shape({
        message: PropTypes.string,
      }),
    }),
  ).isRequired,
};
