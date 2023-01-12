import React, { useMemo } from 'react';

import _ from 'lodash';
import PropTypes from 'prop-types';
import { getTranslate } from 'react-localize-redux';
import { connect } from 'react-redux';
import { useParams, withRouter } from 'react-router-dom';

import MenuItem from 'components/Layout/menu/MenuItem';
import MenuSection from 'components/Layout/menu/MenuSection';
import MenuSubsection from 'components/Layout/menu/MenuSubsection';
import { checkActiveSection, getAllMenuUrls } from 'utils/menu-utils';
import { translateWithDefaultMessage } from 'utils/Translate';

const Menu = ({ menuConfig, location, translate }) => {
  const params = useParams();

  const allMenuUrls = useMemo(() => getAllMenuUrls(menuConfig), [menuConfig]);
  const activeSection = useMemo(() =>
    checkActiveSection({
      menuUrls: allMenuUrls,
      path: location,
      params,
      translate,
    }), [allMenuUrls, location]);

  return (
    <div className="menu-wrapper" id="navbarSupportedContent">
      <ul className="d-flex align-items-center navbar-nav mr-auto flex-wrap">
        { _.chain(menuConfig)
          .filter(section => section.id !== 'configuration')
          .map((section) => {
            if (section.href) {
              return (
                <MenuSection
                  section={section}
                  key={`${section.label}-menu-section`}
                  active={activeSection === section.id}
                />
              );
            }
            if (section.subsections) {
              return (
                <MenuSubsection
                  section={section}
                  key={`${section.label}-menu-subsection`}
                  active={activeSection === section.label}
                />
              );
            }
            if (section.menuItems) {
              return (
                <MenuItem
                  section={section}
                  key={`${section.label}-menuItem`}
                  active={activeSection === section.label}
                />
              );
            }
            return null;
          })
          .value()
      }
      </ul>
    </div>
  );
};

const mapStateToProps = state => ({
  menuConfig: state.session.menuConfig,
  translate: translateWithDefaultMessage(getTranslate(state.localize)),
});

export default withRouter(connect(mapStateToProps)(Menu));

const menuItemPropType = PropTypes.shape({
  label: PropTypes.string,
  href: PropTypes.string,
});

const subsectionPropTypes = PropTypes.shape({
  label: PropTypes.string,
  menuItems: PropTypes.arrayOf(menuItemPropType),
});

const sectionPropTypes = PropTypes.shape({
  label: PropTypes.string,
  href: PropTypes.string,
  subsections: PropTypes.arrayOf(subsectionPropTypes),
  menuItems: PropTypes.arrayOf(menuItemPropType),
});

Menu.propTypes = {
  location: PropTypes.shape({
    pathname: PropTypes.string,
    search: PropTypes.string,
  }).isRequired,
  translate: PropTypes.func.isRequired,
  menuConfig: PropTypes.arrayOf(sectionPropTypes).isRequired,
};
