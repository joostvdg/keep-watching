/*jshint esversion: 6 */

import React from 'react';
import Cookies from 'universal-cookie';

import Button from 'react-bootstrap/lib/Button';
import Modal from 'react-bootstrap/lib/Modal';
import Glyphicon from 'react-bootstrap/lib/Glyphicon'
import Form from 'react-bootstrap/lib/Form';
import FormControl from 'react-bootstrap/lib/FormControl'

import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import Table from 'react-bootstrap/lib/Table';
import Panel from 'react-bootstrap/lib/Panel';
import ButtonToolbar from 'react-bootstrap/lib/ButtonToolbar';
import Badge from 'react-bootstrap/lib/Badge';

import ListGroup from 'react-bootstrap/lib/ListGroup';
import ListGroupItem from 'react-bootstrap/lib/ListGroupItem';
import Label from 'react-bootstrap/lib/Label';

import {ShowMovieList} from '../movies/movies'

const rest = require('rest');
const mime = require('rest/interceptor/mime');


class MoviesWatched extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            watchList: props.watchList,
            moviesCount: 0
        };
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.watchList) {
            console.log('Props set [componentWillReceiveProps]');
        } else {
            console.log('Props not yet set [componentWillReceiveProps]');
        }
    }

    componentDidMount() {
        if (this.state.watchList.id) {
            console.log('Props set');
            const id = this.state.watchList.id;
            let client = rest.wrap(mime);
            client({
                path: '/watchlist/' + id + '/movies',
                headers: {'Accept': 'application/json'}
            }).then(response => {
                let moviesCount = 0;
                if (response) {
                    moviesCount = response.entity.length;
                }
                this.setState({moviesCount: moviesCount});
            });
        } else {
            console.log('Props not yet set');
        }
    }



    render(){
        return (
            <p>
                <Glyphicon glyph="film"/><Badge>{this.state.moviesCount}</Badge>
            </p>
        );
    }
}

export class WatchList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            watchList: props.watchList
        };
    }

    render() {
        const watchList = this.state.watchList;
        return (
            <tr>
                <th>{watchList.name}</th>
                <td><MoviesWatched watchList={watchList} /></td>
                <td><ShowWatchListModal watchList={watchList}/></td>
                <td><ShowWatchListEditModal watchList={watchList} edit="true" /></td>
                <td><DeleteButton watchList={watchList} /></td>
            </tr>
        );
    }
}

class DeleteButton extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            watchList: props.watchList
        };
        this.deleteWatchList = this.deleteWatchList.bind(this);
    }

    deleteWatchList() {
        const id = this.state.watchList.id;
        const cookies = new Cookies();
        const xsrfToken = cookies.get('XSRF-TOKEN');

        let client = rest.wrap(mime);
        client({
            path: '/watchlist/'+id,
            method: 'DELETE',
            headers: {
                'Accept': 'application/json, application/xml, text/plain, text/html, */*',
                'Content-Type': 'application/json',
                'X-XSRF-TOKEN': xsrfToken
            },
            credentials: 'same-origin',
            mode: 'cors',
            redirect: 'follow',
        });
    }

    render() {
        return (
            <Button bsStyle="danger" bsSize="small" onClick={this.deleteWatchList}><Glyphicon glyph="trash"/></Button>
        );
    }
}

export class ShowWatchLists extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            watchLists: ''
        };
        this.fetchData = this.fetchData.bind(this);
    }

    componentDidMount() {
        let client = rest.wrap(mime);
        client({ path: '/watchlist',
            headers: {'Accept': 'application/json'}}).then(response => {
            this.setState({watchLists: response.entity});
        });
    }

    fetchData(){
        let client = rest.wrap(mime);
        client({ path: '/watchlist',
            headers: {'Accept': 'application/json'}}).then(response => {
            this.setState({watchLists: response.entity});
        });
    }

    render() {
        const watchLists = this.state.watchLists;
        const arr = watchLists instanceof Array ? watchLists : [watchLists];
        const watchListItems = arr.map((watchList) =>
            <WatchList watchList={watchList} key={watchList.id} />
        );
        const title = (
            <h3>WatchLists</h3>
        );
        const newWatchList = {
            id: 0,
            name: ''
        };

        return (
            <div>
                <Panel header={title} bsStyle="primary">
                    <ButtonToolbar>
                        <ShowWatchListEditModal watchList={newWatchList} />
                        <Button bsStyle="info" bsSize="large" onClick={this.fetchData}><Glyphicon glyph="refresh"/></Button>
                    </ButtonToolbar>

                    <Table responsive>
                        <thead >
                        <tr>
                            <th>Name</th>
                            <th>In List</th>
                            <th><Glyphicon glyph="zoom-in" /></th>
                            <th><Glyphicon glyph="edit" /></th>
                            <th><Glyphicon glyph="trash" /></th>
                        </tr>
                        </thead>
                        <tbody>
                        {watchListItems}
                        </tbody>
                    </Table>
                </Panel>
            </div>

        );
    }
}


export class ShowWatchListEditModal extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            showModal: false,
            watchList:  props.watchList,
            edit: props.edit,
            showMovies: true
        };
        this.close = this.close.bind(this);
        this.open = this.open.bind(this);
        this.handleChange1 = this.handleChange1.bind(this);
        this.handleSubmit1 = this.handleSubmit1.bind(this);
    }

    close() {
        this.setState({ showModal: false });
    }

    open() {
        this.setState({ showModal: true });
    }

    handleChange1(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const key = target.name;

        const updatedWatchList = this.state.watchList;
        updatedWatchList[key] = value;
        this.setState({
            watchList: updatedWatchList
        });
    }

    handleSubmit1(event) {
        event.preventDefault();

        const cookies = new Cookies();
        const xsrfToken = cookies.get('XSRF-TOKEN');
        // https://hacks.mozilla.org/2016/03/referrer-and-cache-control-apis-for-fetch/
        fetch('/watchlist', {
            method: this.state.edit ? 'POST' : 'PUT',
            headers: {
                'Accept': 'application/json, application/xml, text/plain, text/html, */*',
                'Content-Type': 'application/json',
                'X-XSRF-TOKEN': xsrfToken
            },
            credentials: 'same-origin',
            mode: 'cors',
            redirect: 'follow',
            body: JSON.stringify({
                id: this.state.watchList.id,
                name: this.state.watchList.name
            })
        });

        this.setState({ showModal: false });
        this.setState({ watchList: {name: ''} });
    }

    render() {
        return (
            <div>
                <Button bsStyle="success" bsSize={this.state.edit ? 'small' : 'large'} onClick={this.open}>
                    <Glyphicon glyph={this.state.edit ? 'edit' : 'plus'}/>
                </Button>

                <Modal bsSize="large" show={this.state.showModal} onHide={this.close}>
                    <Modal.Header closeButton>
                        <Modal.Title>WatchList:: {this.state.watchList.name}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Form onSubmit={this.handleSubmit1}>
                            <FormGroup controlId="newWatchListInputName">
                                <ControlLabel>Id</ControlLabel>
                                <FormControl type="text" value={this.state.watchList.id} name="name" readOnly />
                                <FormControl.Feedback />
                            </FormGroup>
                            <FormGroup controlId="newWatchListInputName">
                                <ControlLabel>Name</ControlLabel>
                                <FormControl type="text" value={this.state.watchList.name} name="name" placeholder="name" onChange={this.handleChange1} />
                                <FormControl.Feedback />
                            </FormGroup>
                            <Button type="submit">{this.state.edit ? 'Update' : 'Create'}</Button>
                        </Form>

                        <Panel header='Shared With' bsStyle="primary">
                            <WatchListSharesView watchList={this.state.watchList} />
                        </Panel>

                    </Modal.Body>
                    <Modal.Footer>
                        <Button onClick={this.close}>Close</Button>
                    </Modal.Footer>
                </Modal>
            </div>
        );
    }
}

class WatchListSharesView extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            watchList:  props.watchList,
            watchersSharedWith: {}
        };
    }

    componentDidMount() {
        if (this.state.watchList) {
            const id = this.state.watchList.id;
            let client = rest.wrap(mime);
            client({
                path: '/watchlist/' + id + '/shares',
                headers: {'Accept': 'application/json'}
            }).then(response => {
                if (response) {
                    this.setState({watchersSharedWith: response.entity});
                }
            });
        }
    }

    render() {
        console.log(this.state.watchersSharedWith);
        const shareList = this.state.watchersSharedWith;
        const arr = shareList instanceof Array ? shareList : [shareList];
        const shares = arr.map((share) =>
            <ShowWatchListShared watchListShare={share} key={share.id}  />
        );
        return (
            <ListGroup>
                <ListGroupItem ><ShareWatchListModal watchList={this.state.watchList} /></ListGroupItem>
                {shares}
            </ListGroup>
        );
    }

}

class ShareWatchListModal extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            showModal: false,
            edit: props.edit,
            watcherName: '',
            writeAccess: false,
            watchListName: props.watchList.name,
            watchListId: props.watchList.id
        };
        this.close = this.close.bind(this);
        this.open = this.open.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    close() {
        this.setState({ showModal: false });
    }

    open() {
        this.setState({ showModal: true });
    }

    handleChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }

    handleSubmit(event) {
        event.preventDefault();

        const cookies = new Cookies();
        const xsrfToken = cookies.get('XSRF-TOKEN');
        // https://hacks.mozilla.org/2016/03/referrer-and-cache-control-apis-for-fetch/
        fetch('/watchlist/' + this.state.watchListId + '/shares' , {
            method: this.state.edit ? 'POST' : 'PUT',
            headers: {
                'Accept': 'application/json, application/xml, text/plain, text/html, */*',
                'Content-Type': 'application/json',
                'X-XSRF-TOKEN': xsrfToken
            },
            credentials: 'same-origin',
            mode: 'cors',
            redirect: 'follow',
            body: JSON.stringify({
                watchListId: this.state.watchListId,
                sharerIdentifier: this.state.watcherName,
                hasWriteAccess: this.state.writeAccess
            })
        });

    }

    render() {
        return (
            <div>
                <Button bsStyle="info" bsSize="small" onClick={this.open}>
                    <Glyphicon glyph="plus" />
                </Button>

                <Modal bsSize="large" show={this.state.showModal} onHide={this.close}>
                    <Modal.Header closeButton>
                        <Modal.Title>Share with Watcher</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Form onSubmit={this.handleSubmit}>
                            <FormGroup controlId="newWatchListShareInputName">
                                <ControlLabel>Id</ControlLabel>
                                <FormControl type="text" value={this.state.watchListName} name="name" readOnly />
                                <FormControl.Feedback />
                            </FormGroup>

                            <FormGroup controlId="newWatchListShareInputWatcherID">
                                <ControlLabel>Watcher Id</ControlLabel>
                                <FormControl type="text" value={this.state.watcherName} name="watcherName" placeholder="watcher id" onChange={this.handleChange} />
                                <FormControl.Feedback />
                            </FormGroup>

                            <FormGroup controlId="newWatchListShareInputWriteAccess">
                                <ControlLabel>Write Access</ControlLabel>
                                <FormControl type="checkbox" checked={this.state.writeAccess} value={this.state.writeAccess} name="writeAccess" onChange={this.handleChange} />
                                <FormControl.Feedback />
                            </FormGroup>

                            <Button type="submit">{this.state.edit ? 'Update' : 'Create'}</Button>
                        </Form>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button onClick={this.close}>Close</Button>
                    </Modal.Footer>
                </Modal>
            </div>
        );
    }
}

function ShowWatchListShared(props) {
    const watchListShare = props.watchListShare;
    console.log(watchListShare);
    let name = '';
    let writeAccess = false;
    let identifer = '';
    if (watchListShare.sharedWith) {
        name = watchListShare.sharedWith.name; // TODO: allow people to change their names
        identifer = watchListShare.sharedWith.identifier;
    }
    if (watchListShare.sharedWith) {
        writeAccess = watchListShare.sharedWith.writeAccess;
    }
    return (
        <ListGroupItem ><Glyphicon glyph={writeAccess ? 'pencil' : 'eye-open'}/><Label>{name} ({identifer})</Label></ListGroupItem>
    );
}


export class ShowWatchListModal extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            showModal: false
        };
        this.close = this.close.bind(this);
        this.open = this.open.bind(this);
    }

    close() {
        this.setState({ showModal: false });
    }

    open() {
        this.setState({ showModal: true });
    }

    render() {
        return (
            <div>
                <Button bsStyle="info" bsSize="small" onClick={this.open}>
                    <Glyphicon glyph="zoom-in" />
                </Button>

                <Modal bsSize="large" show={this.state.showModal} onHide={this.close}>
                    <Modal.Header closeButton>
                        <Modal.Title>{this.props.watchList.name}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <ShowWatchList watchList={this.props.watchList} />
                    </Modal.Body>
                    <Modal.Footer>
                        <Button onClick={this.close}>Close</Button>
                    </Modal.Footer>
                </Modal>
            </div>
        );
    }
}

export function ShowWatchList(props) {
    const watchList = props.watchList;
    return (
        <div>
            <ShowMovieList watchListId={watchList.id} />
        </div>
    );
}
