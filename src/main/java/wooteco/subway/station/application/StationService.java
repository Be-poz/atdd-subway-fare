package wooteco.subway.station.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dao.SectionDao;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.exception.DuplicatedStationNameException;
import wooteco.subway.station.exception.NoSuchStationException;
import wooteco.subway.station.exception.StationAlreadyRegisteredInLineException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StationService {
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    @Transactional
    public StationResponse saveStation(StationRequest stationRequest) {
        if (stationDao.isStationExist(stationRequest.getName())) {
            throw new DuplicatedStationNameException();
        }
        Station station = stationDao.insert(stationRequest.toStation());
        return StationResponse.of(station);
    }

    public Station findStationById(Long id) {
        if (stationDao.isStationNotExist(id)) {
            throw new NoSuchStationException();
        }
        return stationDao.findById(id);
    }

    @Transactional
    public List<StationResponse> findAllStationResponses() {
        List<Station> stations = stationDao.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStationById(Long id) {
        if (sectionDao.isStationExistInSection(id)) {
            throw new StationAlreadyRegisteredInLineException();
        }
        if (stationDao.isStationNotExist(id)) {
            throw new NoSuchStationException();
        }
        stationDao.deleteById(id);
    }
}
