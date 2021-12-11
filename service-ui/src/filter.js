import { connect } from 'react-redux';

const Filter = ({count, onFilter}) => (
  <input placeholder={`Filter ${count} items`} onInput={e => {
    onFilter(e.target.value.toLowerCase());
  }}/>
)

const mapStateToProps = state => ({
  count: state.count
});
const mapDispatchToProps = dispatch => ({
  onFilter: filter => dispatch({ type: 'FILTER_CHANGED', filter })
});

export const FilterContainer = connect(mapStateToProps, mapDispatchToProps)(Filter);